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
    b:可以修改内核驱动（/drivers/gpio68.c），则使用 ioctl 来控制使能 <br/>
    
    

