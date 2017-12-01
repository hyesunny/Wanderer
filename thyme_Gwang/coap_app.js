/**
 * Copyright (c) 2016, OCEAN
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Created by Il-Yeup Ahn on 2016-11-18.
 */

var coap = require('coap');
var fs = require('fs');
var bodyParser = require('body-parser');
var mqtt = require('mqtt');
var util = require('util');
var xml2js = require('xml2js');
var url = require('url');
var ip = require('ip');
var shortid = require('shortid');

global.sh_adn = require('./coap_adn');
var noti = require('./noti');
var tas = require('./tas');

var COAP_SUBSCRIPTION_ENABLE = 0;
var MQTT_SUBSCRIPTION_ENABLE = 0;


// ?????? ????????.
var coap_server = null;
var noti_topic = '';

// ready for mqtt
for(var i = 0; i < conf.sub.length; i++) {
    if(conf.sub[i].name != null) {
        if(url.parse(conf.sub[i].nu).protocol === 'coap:') {
            COAP_SUBSCRIPTION_ENABLE = 1;
            if(url.parse(conf.sub[i]['nu']).hostname === 'autoset') {
                conf.sub[i]['nu'] = 'coap://' + ip.address() + ':' + conf.ae.appport + url.parse(conf.sub[i]['nu']).pathname;
            }
        }
        else if(url.parse(conf.sub[i].nu).protocol === 'mqtt:') {
            MQTT_SUBSCRIPTION_ENABLE = 1;
        }
        else {
            console.log('notification uri of subscription is not supported');
            process.exit();
        }
    }
}

var return_count = 0;
var request_count = 0;


function ready_for_notification() {
    if(COAP_SUBSCRIPTION_ENABLE == 1) {
        coap_server = coap.createServer();
        coap_server.listen(conf.ae.port, function() {
            console.log('coap_server running at ' + conf.ae.port +' port');
        });
        coap_server.on('request', coap_message_handler);
    }
    else if(MQTT_SUBSCRIPTION_ENABLE == 1) {
        if(conf.cse.id.split('/')[1] === '') {
            var cseid = conf.cse.id.split('/')[0];
        }
        else {
            cseid = conf.cse.id.split('/')[1];
        }

        for(var i = 0; i < conf.sub.length; i++) {
            if (conf.sub[i].name != null) {
                if (url.parse(conf.sub[i]['nu']).hostname === 'autoset') {
                    conf.sub[i]['nu'] = 'mqtt://' + conf.cse.host + '/' + conf.ae.id;
                    noti_topic = util.format('/oneM2M/req/%s/%s/#', cseid, conf.ae.id);
                }
                else if (url.parse(conf.sub[i]['nu']).hostname === conf.cse.host) {
                    noti_topic = util.format('/oneM2M/req/%s/%s/#', cseid, conf.ae.id);
                }
                else {
                    noti_topic = util.format('%s', url.parse(conf.sub[i].nu).pathname);
                }
            }
        }
        mqtt_connect(conf.cse.host, noti_topic);
    }
}


function create_cnt_all(count, callback) {
    sh_adn.crtct(count, function (rsc, res_body, count) {
        console.log('x-m2m-rsc : ' + rsc + ' <----');
        if(rsc == 5106 || rsc == 2001 || rsc == 4105) {
            count++;
            if(conf.cnt.length > count) {
                create_cnt_all(count, function (rsc, count) {
                    callback(rsc, count);
                });
            }
            else {
                callback(rsc, count);
            }
        }
        else {
            callback('9999', count);
        }
    });
}

function delete_sub_all(count, callback) {
    sh_adn.delsub(count, function (rsc, res_body, count) {
        console.log('x-m2m-rsc : ' + rsc + ' <----');
        if(rsc == 5106 || rsc == 2002 || rsc == 2000 || rsc == 4105 || rsc == 4004) {
            count++;
            if(conf.sub.length > count) {
                delete_sub_all(count, function (rsc, count) {
                    callback(rsc, count);
                })
            }
            else {
                callback(rsc, count);
            }
        }
        else {
            callback('9999', count);
        }
    });
}

function create_sub_all(count, callback) {
    sh_adn.crtsub(count, function (rsc, res_body, count) {
        console.log('x-m2m-rsc : ' + rsc + ' <----');
        if(rsc == 5106 || rsc == 2001 || rsc == 4105) {
            count++;
            if(conf.sub.length > count) {
                create_sub_all(count, function (rsc, count) {
                    callback(rsc, count);
                })
            }
            else {
                callback(rsc, count);
            }
        }
        else {
            callback('9999', count);
        }
    });
}

function ae_response_action(status, res_body, callback) {
    if(conf.ae.bodytype === 'xml') {
        var message = res_body;
        var parser = new xml2js.Parser({explicitArray: false});
        parser.parseString(message.toString(), function (err, result) {
            if (err) {
                console.log('[rtvae xml2js parser error]');
            }
            else {
                var aeid = result['m2m:ae']['aei'];
                conf.ae.id = aeid;
                callback(status, aeid);
            }
        });
    }
    else {
        var result = JSON.parse(res_body);
        var aeid = result['m2m:ae']['aei'];
        conf.ae.id = aeid;
        callback(status, aeid);
    }
}

function coap_watchdog() {
    if(sh_state === 'crtae') {
        console.log('[sh_state] : ' + sh_state);
        sh_adn.crtae(function(status, res_body) {
            //console.log(res_body + ' <-----');
            if(status == 5106 || status == 2001) {
                ae_response_action(status, res_body, function (status, aeid) {
                    console.log('x-m2m-rsc : ' + status + ' - ' + aeid + ' <----');
                    sh_state = 'crtct';
                });
            }
            else if(status == 4105) {
                console.log('x-m2m-rsc : ' + status + ' <----');
                sh_state = 'rtvae'
            }
        });
    }
    else if(sh_state === 'rtvae') {
        if(conf.ae.id === 'S') {
            conf.ae.id = 'S' + shortid.generate();
        }

        console.log('[sh_state] : ' + sh_state);
        sh_adn.rtvae(function(status, res_body) {
            if(status == 2000) {
                ae_response_action(status, res_body, function (status, aeid) {
                    console.log('x-m2m-rsc : ' + status + ' - ' + aeid + ' <----');
                    sh_state = 'crtct';
                });
            }
            else {
                console.log('x-m2m-rsc : ' + status + ' <----');
            }
        });
    }
    else if(sh_state === 'crtct') {
        console.log('[sh_state] : ' + sh_state);
        request_count = 0;
        return_count = 0;
        create_cnt_all(0, function(status, count) {
            if(conf.cnt.length <= count) {
                sh_state = 'delsub';
            }
        });
    }
    else if(sh_state === 'delsub') {
        console.log('[sh_state] : ' + sh_state);
        request_count = 0;
        return_count = 0;
        delete_sub_all(0, function(status, count) {
            if(conf.sub.length <= count) {
                sh_state = 'crtsub';
            }
        });
    }

    else if(sh_state === 'crtsub') {
        console.log('[sh_state] : ' + sh_state);
        request_count = 0;
        return_count = 0;

        create_sub_all(0, function(status, count) {
            if(conf.sub.length <= count) {
                sh_state = 'crtci';

                ready_for_notification();

                tas.ready();
            }
        });
    }
    else if(sh_state === 'crtci') {

    }
}

wdt.set_wdt(require('shortid').generate(), 2, coap_watchdog);


// for notification
var xmlParser = bodyParser.text({ type: '*/*' });

var noti_count = 0;
function coap_message_handler(request, response) {

    var headers = {};
    headers['X-M2M-TY'] = '';

    // check coap options
    for (var idx in request.options) {
        if (request.options.hasOwnProperty(idx)) {
            if (request.options[idx].name === '256') { // 'X-M2M-Origin
                headers['X-M2M-Origin'] = request.options[idx].value.toString();
            }
            else if (request.options[idx].name === '257') { // 'X-M2M-RI
                headers['X-M2M-RI'] = request.options[idx].value.toString();
            }
            else if (request.options[idx].name === '267') { // 'X-M2M-TY
                headers['X-M2M-TY'] = request.options[idx].value.toString();
            }
        }
    }

    if(request.headers['Accept'])
    {
        headers['Accept'] = request.headers['Accept'];
    }

    if(request.headers['Content-Type'])
    {
        if(headers['X-M2M-TY'] === '') {
            headers['Content-Type'] = request.headers['Content-Type'];
        }
        else {
            headers['Content-Type'] = request.headers['Content-Type'] + ';ty=' + headers['X-M2M-TY'];
        }
    }

    delete headers['X-M2M-TY'];

    noti_count = 0;
    for(var i = 0; i < conf.sub.length; i++) {
        if(conf.sub[i]['nu'] != null) {
            var nu_path = url.parse(conf.sub[i]['nu']).pathname.toString().split('/')[1];
            if (nu_path === request.url.split('/')[1].split('?')[0]) {
                noti_count++;
                console.log('[CO notification through coap <-- ' + headers['X-M2M-Origin'] + ']');

                var bodytype = headers['Content-Type'].split('/')[1];
                if(bodytype !== 'json' && bodytype !== 'xml') {
                    bodytype = bodytype.split('+')[1];
                }
                if (bodytype === 'json') {
                    try {
                        var pc = JSON.parse(request.payload.toString());
                        var rqi = headers['X-M2M-RI'];

                        noti.coap_noti_action(rqi, pc, 'json', response);
                    }
                    catch (e) {
                        console.log(e);
                    }
                }
                else {
                    var parser = new xml2js.Parser({explicitArray: false});
                    parser.parseString(request.payload.toString(), function (err, pc) {
                        if (err) {
                            console.log('[http noti xml2js parser error]');
                        }
                        else {
                            var rqi = request.headers['x-m2m-ri'];

                            noti.coap_noti_action(rqi, pc, 'xml', response);
                        }
                    });
                }
            }
        }
    }
}

function mqtt_connect(serverip, noti_topic) {
    mqtt_client = mqtt.connect('mqtt://' + serverip + ':' + conf.cse.mqttport);

    mqtt_client.on('connect', function () {
        mqtt_client.subscribe(noti_topic);
        console.log('[mqtt_connect] noti_topic : ' + noti_topic);
    });

    mqtt_client.on('message', function (topic, message) {

        var topic_arr = topic.split("/");

        var bodytype = conf.ae.bodytype;
        if(topic_arr[5] != null) {
            bodytype = (topic_arr[5] === 'xml') ? topic_arr[5] : ((topic_arr[5] === 'json') ? topic_arr[5] : 'json');
        }

        if(topic_arr[1] === 'oneM2M' && topic_arr[2] === 'req' && topic_arr[4] === conf.ae.id) {
            if(bodytype === 'xml') {
                var parser = new xml2js.Parser({explicitArray: false});
                parser.parseString(message.toString(), function (err, jsonObj) {
                    if (err) {
                        console.log('[mqtt noti xml2js parser error]');
                    }
                    else {
                        noti.mqtt_noti_action(topic_arr, jsonObj);
                    }
                });
            }
            else { // json
                var jsonObj = JSON.parse(message.toString());
                noti.mqtt_noti_action(topic_arr, jsonObj);
            }
        }
        else {
            console.log('topic is not supported');
        }
    });
}
