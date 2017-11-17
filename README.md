# FileSelectDemo
Android版本地文件选择器

如何配置

在工程根目录下面的build.gradle下添加如下代码：
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
在要引用的module的build.gradle下添加如下代码：
	dependencies {
	        compile 'com.github.Duckdan:FileSelectDemo:v1.0'
	}

如何在引用时出现引用资源的冲突，那么将module中的build.gradle改成如下代码：
dependencies {
    ...
    compile 'com.android.support:appcompat-v7:25.3.1'  //比它低的版本也可以
    compile 'com.github.Duckdan:FileSelectDemo:v1.0'
   
}

具体详解可以去查看我的博客，地址如下所示
http://blog.csdn.net/Duckdan/article/details/78563486
 
