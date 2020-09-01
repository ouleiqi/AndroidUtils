# AndroidUtils

该功能库为android 7.1.2 智能终端常用功能的集合 <br/>

## 目录说明： 
```
 src/main/java-public        应用程序代码 <br/>
         /res-public         应用程序资源路径 <br/>
         /jni-libs-public    应用程序引用的native libs 路径 <br/>        
         /java-utils         工具功能常用代码 <br/>
         /res-utils          工具功能资源路径
         /jni-source-utils   native 源码 <br/>
```            
           
## 工具功能

### 串口|RS485
1. 全双工，串口通信 SerialPort.java， 使用jni打开设备节点，返回文件描述符，进而获取输入输出流。
实现方式:<br/>
```
    String enable = "/dev/gpio68"; //  手动使能某个节点 system(echo 1 > /dev/gpio68)。 若不需要传null
    new SerialPort(new File("/dev/ttyS0"), 9600, 0, enable);
    
```
2. RS485 通信  RS485SerialPortUtil.java (使用 SerialPort.java) 来实现，手动使能GPIO控制收发

3. RS485 通信  RS485SerialPortUtilNew.java ，手动使能GPIO控制收发<br/>
   发送使能：使用 ioctl 来保证发送完成<br/>
   接收使能，分两种情况：<br/>
    a:条件限制，无法修改驱动，则依旧使用 sytem(echo 1 > /dev/gpio68) 来使能接收<br/>
    b:可以修改内核驱动（/drivers/gpio/gpio68.c），则使用 ioctl 来控制使能 <br/>

### USB串口
使用 SerialInputOutputManager.java， 复制自：https://github.com/mik3y/usb-serial-for-android    
    
### 控制状态栏、导航栏的显示隐藏
该功能需要修改android代码, 具体方式见 /framework-modify/动态控制显示隐藏导航栏和状态栏/<br/>
```
    // 隐藏导航栏及状态栏
    sendBroadcast(new Intent("android.intent.action.HIDE_NAVIGATION_BAR"));
    sendBroadcast(new Intent("android.intent.action.HIDE_STATUS_BAR"));
    // 显示导航栏及状态栏
    sendBroadcast(new Intent("android.intent.action.SHOW_NAVIGATION_BAR"));
    sendBroadcast(new Intent("android.intent.action.SHOW_STATUS_BAR"));
      
```
### 动态权限申请
一次请求单个或多个权限
```
    // 1. 申请的权限必须先在manifest中配置， 否则申请结果总是失败
    // 2. ACCESS_FINE_LOCATION 权限包含 ACCESS_COARSE_LOCATION 粗略定位权限
    // 3. 按照权限请求顺序进行申请，遇到失败则返回，之后权限则处于未申请状态: -1
    String permissions[] = new String[]{ Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION};
    int requestCode = 7777;
    PermissionHelper.request(TCMainActivity.this, permissions, requestCode,new PermissionHelper.Callback(){

        @Override
        public void onResult(int requestCode, String[] permissions, int[] grantResult) {
            if(PackageManager.PERMISSION_GRANTED == grantResult[0]){
                // 第一个权限 android.permission.CAMERA 授权成功
            }
            // 授权结果
            Toast.makeText(TCMainActivity.this, "请求权限：" +Arrays.toString(permissions) + "  授权结果:" + Arrays.toString(grantResult), Toast.LENGTH_LONG).show();
        }
    });
```
