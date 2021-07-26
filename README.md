# android_green_matting
android camera green matting, android版本的绿幕抠图
# ios_green_matting
ios版本的绿幕抠图, 各种绿色完整的抠去. 

- SDK是我们商用版本, 付费. 
- 此为最简单的演示, 方便您评估使用.
- 我们完整的演示请下载:
- https://www.pgyer.com/LanSongIOS
- https://www.pgyer.com/LanSongAndroid




### 完整功能介绍(中文版本)
- 输入和控制功能:相机的图像采集, 相机控制, 画面UI布局, 设置背景图片或视频, 增加挂件, 相机画面的缩放移动手指, 贴纸,绿幕抠图调节, 上层的UI自定义显示, 各种其他的图像输入接口(多机位), 这些多图像的层级调节等
- 输出的功能: 把所有的输入图层, 最后合成一帧画面, 实时的引出
- 蓝松SDK已经把这些都封装完毕,命名为LSOCameraLive, 每个功能几乎都是一行代码调用即可. 方便您的使用.

####  其他 
- 如果您需要另外的想自己控制所有的输入叠加输出,则我们可以提供最纯净的抠绿API,即图像输入, 抠绿, 图像输出;

#### 1. 抠绿效果
- 支持不同程度的绿色, 比如深绿,浅绿, 这些绿色可在同一个画面中, 均可自然抠去. 
- 提供色彩保护调节和抠图强度调节功能, 方便在复杂的场景下使用.(大部分并不需要调节).

#### 2. 叠加背景
- 图片最大可5000x8000分辨率. 视频最大可4k分辨率.
- 视频和图片可快速切换. 
- 视频和和图片的位置大小可手动实时调节.
- 视频和图片可做滤镜处理,比如高斯模糊处理等.
 
 #### 3. 摄像头和美颜
- 默认为竖屏全屏显示, 可调节前后镜头, 曝光度, 上下镜像, 左右镜像, 隐藏等功能;
- 支持全屏, 9:16, 3:4分辨率设置. 
- 摄像头画面可移动旋转缩放, 可倒角设置, 可自定义倒脚形状样式等.
- 已接入相芯美颜, 可外部接入第三方SDK. android以纹理的形式引出,处理后以纹理的形式返回; ios以sampleBuffer的形式输出和输入;
    
#### 4. 外部输入和调节
- 多机位接口, 我们预留了surface的接口, 支持MediaPlayer, ijkPlayer等支持setSurface的播放器接入.
- 小挂件, 支持背景是纯绿色的图片视频, 图片视频加入后, 默认去掉绿色成透明画面, 最大可同时10个视频.
- 自定义UI. 支持自定义UI控件, 比如常见的按钮, 图片,文本框等,这些UI界面会作为一层叠加进去, 在导出数据时一并引出; 
- 外部usb摄像头. uvc开源控件有setSurface接口,支持把外部的usb摄像头选择到我们提供的surface接口上.
- 图层调节, 支持图层上下位置调节,常见使用在多机位场合,把不同的画面显示到下面或上面;
    
#### 5. 蓝牙键盘. 
- 支持常见蓝牙键盘，支持常见直播用到的航世蓝牙数字小键盘等
 		
#### 6. 数据引出(推流)
- 各种图层叠加后, 以帧的形式引出,android端是nv21格式. ios端是bgra; 
- 可实时修改引出帧的宽度和高度, 以适应不同的网络带宽;
- 外部设置的UI界面, 也会和其他图层叠加, 一并引出;


### Complete function introduction (English version)
- Input and control functions: camera acquisition, camera control, picture UI layout, setting background picture or video, adding pendant, camera picture scaling, moving fingers, stickers, green screen matting adjustment, upper UI custom display, various other image input interfaces (multiple positions), hierarchical adjustment of these multiple images, etc
- Output function: synthesize all input layers into one frame and export them in real time
- Lansong SDK has encapsulated all these functions and named them lsocameralive. Each function can be called in almost one line of code. It is convenient for you to use
#### Other
- If you want to control all input and output by yourself, we can provide the purest green matting API, that is, image input, green matting and image output;

#### 1. Green effect
- Support different levels of green, such as dark green and light green, which can be naturally removed in the same picture
- It provides color protection adjustment and matting intensity adjustment functions, which is convenient for use in complex scenes (most do not need adjustment)
#### 2. Overlay background
- The maximum resolution of picture is 5000x8000. The maximum resolution of video is 4K
- Video and pictures can be switched quickly
- The position and size of video and pictures can be adjusted manually and in real time
- Video and pictures can be processed by filter, such as Gaussian blur
#### 3. Camera and beauty
- The default is vertical full screen display, which can adjust the front and rear lenses, exposure, up and down mirror images, left and right mirror images, hiding and other functions;
- Supports full screen, 9:16, 3:4 resolution settings
- The camera picture can be moved, rotated, scaled, chamfered, customized inverted foot shape and style, etc
- It has been connected to phase core beauty, and can be externally connected to a third-party SDK. Android is exported in the form of texture, and returned in the form of texture after processing; IOS outputs and inputs in the form of samplebuffer;
#### 4. External input and adjustment
- Multi slot interface. We reserve the interface of surface, which supports mediaplayer, ijkplayer and other players that support setsurface
- The small pendant supports pictures and videos with pure green background. After pictures and videos are added, the green will be removed by default and become a transparent picture. Up to 10 videos can be added at the same time
- User defined UI. Support user-defined UI controls, such as Button, TextView, ImageView, etc. These UI interfaces will be superimposed as a layer and exported when exporting data;
- External USB camera. UVC open source control has setsurface interface, which supports selecting external USB camera to the surface interface provided by us
- Layer adjustment supports layer up and down position adjustment. It is commonly used in multi stand occasions to display different pictures below or above;
#### 5. Bluetooth keyboard
- Support common Bluetooth keyboards, Hangshi Bluetooth digital keypad used for common live broadcasting, etc
#### 6. Data export (streaming)
- After various layers are superimposed, they are exported in the form of frames. The Android end is nv21 format. The IOS end is bgra;
- The width and height of the outgoing frame can be modified in real time to adapt to different network bandwidth;
- The layers will be superimposed with other UI settings;

cocat us:
- email: support@lansongtech.com
- web: www.lansongtech.com