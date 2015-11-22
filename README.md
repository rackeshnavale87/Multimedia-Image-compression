# Multimedia-Image-compression
Multimedia Image compression for the various purpose, using different algorithms as explained in the Readme.txt

Take an image an input in a 4:3 aspect ratio, which will be a high resolution image (4000x3000) or a low resolution image (400x300). Your program will generate an output image which will be one of the following standard formats
-  O1: 1920x1080
-  O2: 1280x720
-  O3: 640x480


In each case, depending on your input size, you will need to either down sample or up sample the image. In each case implement these two methods to choose your sample value.

In the down sample case, use


1. Specific/Random sampling where you choose a specific pixel
2. Gaussian smoothing where you choose the average of a set of samples


 In the up sample case, use


1. Nearest neighbor to choose your up sampled pixel
2. Bilinear/Cubic interpolation
