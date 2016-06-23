# dontForgetMe
This is a school project to show Jade (Java multi-agent platform)
capabilities. It simulates the detection process of a baby forgotten
on a car and it sends a message to inform the situation.

## Usage
Download the [Jade library](http://jade.tilab.com/) and make sure to add it to
your Java classpath (don't worry if you can't set the Java classpath, there are insctuctions below to run the project without it).

You can import the project to Intellij Idea, add the Jade
dependence and run it.

##### Or
If you want to compile and run it all from the console:

###### Assuming you have already set Jade to your Java classpath
- Compile all the Java classes contained on the project (`src/agents/*` and `src/Main.java`):
```shell
javac PAHT_TO_THE_PROJECT/src/agents/* PAHT_TO_THE_PROJECT/src/Main.java
```
- Go to `project_location/src`
- Run it:
```shell
java -classpath . Main
```

###### Assuming you haven't set Jade to your Java classpath
- Compile all the Java classes contained on the project (`src/agents/*` and `src/Main.java`):
```shell
javac -cp PATH_TO_JADE/jade/lib/jade.jar PAHT_TO_THE_PROJECT/src/agents/* PAHT_TO_THE_PROJECT/src/Main.java
```
- Go to `project_location`
- Run it:
```shell
java -classpath PATH_TO_JADE/jade/lib/jade.jar:src Main
```
###### Note:
Every time you run the project you will see on the sniffer and console one
of the three simulation cases. It will depend on the simulation process.