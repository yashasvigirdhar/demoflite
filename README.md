demoflite
=========

for playing with the flite code:

added javacallbacks:

for now, the app crashes due to "jni detach thread error", but the callbacks have been tested by printing in the log.

steps:

->run the app.

->go to android->settings->language and input -> text-to-speech output

->choose tts

->listen to an example

when the flite reads this sample text, there are callbacks from c to java, for every word, tested using log.

NOTE: first download the voices for flite manually from http://tts.speech.cs.cmu.edu/android/flite-data.tar.bz2.
and place them in the flite directory.
