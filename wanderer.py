#import the necessary packages
from picamera.array import PiRGBArray
from picamera import PiCamera
import time
import cv2

cascade = cv2.CascadeClassifier("/home/pi/opencv/opencv-3.3.1/data/haarcascades/haarcascade_frontalface_alt.xml")

def detect(img, cascade):
    rects = cascade.detectMultiScale(img, scaleFactor = 1.3, minNeighbors = 4, minSize = (30, 30), flags = cv2.CASCADE_SCALE_IMAGE)
    if len(rects) ==0:
        return []
    rects[:,2:] += rects[:,:2]
    return rects

def draw_rects(img, rects, color):
    for x1, y1, x2, y2 in rects:
        cv2.rectangle(img, (x1, y1), (x2, y2), color, 2)

#initialize the camera and grab a reference to the raw camera capture
camera = PiCamera()
camera.resolution = (640, 480)
camera.framerate = 32
rawCapture = PiRGBArray(camera, size = (640, 480))

# cascade = cv2.CascadeClassifier("opencv-3.3.1/data/haarcascades/haarcascade_frontalface_alt.xml")

#allow the camera to warmup
time.sleep(0.1)

#capture frames from the camera
count =0
for frame in camera.capture_continuous(rawCapture, format="bgr", use_video_port=True):
    #grab the raw Numpy array representing the image, the initialize the timestamp
    #and occupied/unoccupied text
    img = frame.array

    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    gray = cv2.equalizeHist(gray)

    rects = detect(gray, cascade)
    vis = img.copy()
    draw_rects(vis, rects, (0, 255, 9))
    if len(rects)!=0:
        count += 1
        print(str(count) + "Face detected")
    #show the fame
    cv2.imshow("Frame", vis)
    key = cv2.waitKey(1) & 0xFF

    #clear the stream in preparation for the next frame
    rawCapture.truncate(0)

    #if the 'q' key was pressed, break from the loop
    if key == ord('q'):
        break
