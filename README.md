# JSch for JDK 9
Is an improved implementation of JSch (http://www.jcraft.com/jsch/, a pure Java implementation of SSH2) with support for the Java Platform Module System.

## Additional features and improvements
### ChannelWritableShell
An version of _ChannelShell_ than not use an _InputStream_ for send data to server. Instead that, method _send(byte[])_ must be used.

### Session#sendKeepAliveMsg()
It's behavior was changed for allowing to execute an _Runnable_ instead send the original keepalive message. This _Runnable_ can be defined with _Session#setCustomSendKeepAliveMsg(Runnable)_ method. If _Runnable_ isn't defined, original keepalive message will be sending.