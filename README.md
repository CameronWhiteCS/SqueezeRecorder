# SqueezeRecorder

SqueezeRecorder is a free and open source macro recorder designed specifically for Old School Runescape. It allows you to record your mouse movements, mouse clicks (including shift clicking), and some key presses and then play them back on a loop. Recordings can even be shared with your friends thanks to SqueezeRecorder's relative coordinate system. SqueezeRecorder works on all operating systems and all OSRS clients.  

# Benefits

Becuase SqueezeRecorder does not directly interact with the game client (no injection/reflection) and uses human-generated mouse data, many components of Jagex's anti-cheating software cannot reliably detect it when it's used properly. During development, I was able to get 99 fletching in less than two weeks using a single hour-long recording. My botting sessions were between 6 and 18 hours at a time. 

# Command Line Arguments

*To make life easier for users less familiar with command line programs, I have included example .bat and .sh files. Just adjust the settings within those files to yuor liking*

SqueezeRecorder can be used both as a command line program or using its GUI. All arguments are key-value pairs are are provided to the program using the format ```key=value```. The following command line arguments are available (WARNING: neither keys nor values can contain any spaces! This includes file names):

* x (integer) - The absolute x coordinate of the top-left corner of the OSRS client. Defaults to 0.
* y (integer) - The absolute y coordinate of the top-left corner of the OSRS client. Defaults to 0.
* file (string) - The file path of the recording being created or being used. 
* recording (true/false) - True indicates that the program should record mouse movements while false indicates that the program should play back the contents of the provided file . Defaults to false.
* looping (true/false) - If recording is set to true, looping determines whether or not the provided file should be played on repeat continuously or just once. 
* gui (true/false) - Whether or not to display the GUI. Defaults to true. 

So, an example command line configuration would be as follows:

```
java -jar SqueezeRecorder.jar x=500 y=500 file=/home/cameron/Desktop/stringing_bows.txt looping=true recording=false gui=false
```

If the GUI is enabled, all other command line arguments **will be ignored**. 

# Key bindings

To stop recording, press the escape key. To pause or resume while recording, press num lock. These keys can be pressed from anywhere and the command prompt/terminal does not need to be visible. 

To stop playing a recording, use ALT + TAB to switch to the command prompt running SqueezeRecorder and press CTRL + C. 

# Download

A download to SqueezeRecorder can be found above inside the "release" folder. 


# Building

SqueezeRecorder's only dependency is [JNativeHook](https://github.com/kwhat/jnativehook). You will need to add it to your build path before compiling SqueezeRecorder. 
