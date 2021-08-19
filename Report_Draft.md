# Google Summer of Code 2021 Final report

This is the final report of the project undertaken by me during the Google Summer of Code 2021. This report contains relevant information pertaining to all the work that I experimented out during the GSoC timeline, from May 2021 to August 2021. 

## Student Name

[Nikita Mahoviya](https://github.com/nikitamahoviya) 

## Organization

[National Resource for Network Biology](https://github.com/nrnb)

## Project Title

[Develop GUI for ImageJ groovy script calling VCell API](https://summerofcode.withgoogle.com/projects/#5521664948830208) 


## Mentors 
- [Ann Cowan](https://github.com/ACowan0105) 
- [Frank Morgan](https://github.com/vcfrmgit) 
- [Michael Blinov](https://github.com/vcellmike) 

## Project Overview

We have to create a service for VCell users that allows Fiji/ImageJ scripting to directly access the VCell client and then expand this service into a series of user-friendly plugins for ImageJ that will automate processing and analyzing cell imaging simulation experiments. The task will include:
Entering the appropriate images to create a geometry
Setting initial conditions for the simulation
Running multiple simulations with varying parameter sets and
Visualizing and comparing simulation results to the original experimental image set

## Past Synopsis of the Project

Earlier the ImageJ user had to write scripts in Groovy in an interface called Macros which automates all the commands. These commands were the information related to VCell User ID, simulation, variable, and other parameters according to the result required. There was no such package for ‘plugins’ that could eventually be a part of the menu-structure

## Tasks Done During the Coding Period
The coding period was all about exploring and implementing various ideas for designing a menu structure that could bring out various working plugins. The project was based on GUI development hence it was not just based on coding but also for showing the creativity on GUI to make it more attractive and appealing.

To begin with, I started learning about the syntaxes involved in Groovy and then proceeded towards its conversion into Java. I experimented with the code for various situations to make the most appropriate keeping in mind the appearance and ease of accessibility.

The project was basically to design a menu structure that could give us options to choose from various ImageJ plugins calling VCell API. These plugins are basically Groovy files which when run in Macros produce the desired results. The workflow was such that on launching the ImageJ interface, we got many options to explore in the toolbar, the ‘plugins’ option is the one that suits our needs.



So in this menu structure, we have Plugins that have various options like VCell Plugins and VCell Help which in turn have a list of functionalities to offer.

#### Designing the menu structure

For designing the menu structure, I referred [ClearRecent.java](https://github.com/scijava/scijava-plugins-commands/blob/35790dea3819a7e46b3741abc179505d56e84a17/src/main/java/org/scijava/plugins/commands/io/ClearRecent.java) which included:

- The **Header** under which the plugin will be placed
- The **icon image** to clarify the purpose and make the plugin stand out
- The **hierarchy** or the serial order in which the plugin should appear
- The **short-cut keys** to access the plugin with keyboard

Taking the example of 



#### [VCell-Plugins](https://github.com/nikitamahoviya/Plugins_VCell-ImageJ/tree/main/VCell-Plugins)



The VCell Plugins has a drop-down list of Plugins that are in communication with VCell:



[**Line Plot**](https://github.com/nikitamahoviya/Plugins_VCell-ImageJ/blob/main/VCell-Plugins/LinePlot.java) - The Groovy script [chart.groovy](https://github.com/virtualcell/vcell/blob/master/vcell-imagej-helper/chart.groovy) is converted into a Java plugin which results in 2 plots
- Line Plot by Time
- Time Plot by Distance
It takes up data like, ModelType, VCell User ID, VCell User Name, Application, Simulation, Variable, TimePont, Start and End Index.

[**VCell Model Search**](https://github.com/nikitamahoviya/Plugins_VCell-ImageJ/blob/main/VCell-Plugins/VCellPlugin.java) - The Groovy script []() is converted into a Java plugin. In addition to just the direct conversion, it has certain additional features like selecting the various variables and suitable times through sliders. By default, it takes up data such as  ModelType, VCell User ID, VCell User Name, Application, and then accordingly loads the Simulation, Variable values


#### [VCell-Help](https://github.com/nikitamahoviya/Plugins_VCell-ImageJ/tree/main/VCell-Help)



I designed VCell Help with an objective that it could be resourceful to future users and developers. If someone wants to create plugins out of some Groovy scripts then they may refer to these as how elements are added to a GUI and make them functional.

- [**VCell ImageJ-Help**](https://github.com/nikitamahoviya/Plugins_VCell-ImageJ/tree/main/VCell-Help) - This plugin opens into a GUI which retrieves information from your machine, like your Operating System, Architecture, and returns it. Informs about the prerequisites for running the VCell plugins and also gives online assistance by directing to the browser with a click

- [**VCell ImageJ Groovy Scripts**](https://github.com/nikitamahoviya/Plugins_VCell-ImageJ/blob/main/VCell-Help/Groovy_Reference.java) - This plugin opens into a GUI that gives information about running the various groovy scripts in Macros and also directs the user to various scripts on Github

- [**VCell ImageJ Template**](https://github.com/nikitamahoviya/Plugins_VCell-ImageJ/blob/main/VCell-Help/Template.java) - This plugin is not related to any functionality but is a reference for future developers which might help them in making their own plugins It has various commands for text editing, adding buttons, browsing files, etc. Some of the features I had referred from [WidgetDemo.java](https://github.com/imagej/tutorials/blob/d3ff8e818bb26cb4713371878b239b36cb7d4877/howtos/src/main/java/howto/ui/WidgetDemo.java) 

- [**VCell ImageJ Template Example**](https://github.com/nikitamahoviya/Plugins_VCell-ImageJ/blob/main/VCell-Help/Template.java) - This plugin is another template that is functional and tells how we can implement various elements in a GUI which can run as a plugin. 

## For Future Reference

These java files are for future reference purposes. During the course of time I had tried out various permutations with the plugins and for that made many changes in the existing ones from making them easily understandable to creating a whole new file.

### The 'Minimal' Files

A lot of time gets elapsed till the result gets loaded after clicking ‘OK’ on the information displayed on the GUI. So to make this elapsing time attractive, there is an addition of a code segment that shows the ‘percentage’ loaded. 

To understand the overall functionality of code easily, we can get rid of these progress bars and even understand what segment of code they are affecting other than just showing the progress. This way we can easily understand the dependency of various segments.

#### [ModelLoad_Minimal](https://github.com/nikitamahoviya/Plugins_VCell-ImageJ/blob/main/Backup/ModelLoad_Minimal.java) 
This is a reduced version of [ModelLoad](https://github.com/virtualcell/vcell/blob/master/vcell-imagej-helper/src/main/java/org/vcell/imagej/plugin/ModelLoad.java).  , whose sole purpose is to display the GUI and the resulting image. The segment showing the GUI loading time and the time elapsed in loading the result has been omitted. This way it's just a file performing a minimum number of tasks.

#### [VCellPlugin_Minimal](https://github.com/nikitamahoviya/Plugins_VCell-ImageJ/blob/main/Backup/VCellPlugin_Minimal.java) 
This is a reduced version of [VCellPlugin](https://github.com/virtualcell/vcell/blob/master/vcell-imagej-helper/src/main/java/org/vcell/imagej/plugin/VCellPlugin.java]) , whose sole purpose is to display the GUI and the resulting image. The segment showing the GUI loading time and the time elapsed in loading the result has been omitted. This way it's just a file performing a minimum number of tasks.

### Other Files

#### [LinePlot_Search](https://github.com/nikitamahoviya/Plugins_VCell-ImageJ/blob/main/Backup/LinePlot_Search.java) 
It was an attempt to combine  [VCellPlugin](https://github.com/virtualcell/vcell/blob/master/vcell-imagej-helper/src/main/java/org/vcell/imagej/plugin/VCellPlugin.java)   and [ModelLoad](https://github.com/virtualcell/vcell/blob/master/vcell-imagej-helper/src/main/java/org/vcell/imagej/plugin/ModelLoad.java) to give an interface in which the user doesn't have to type in the information but just select the most appropriate one from a drop-down list.

#### [ModelLoad_Analyse](https://github.com/nikitamahoviya/Plugins_VCell-ImageJ/blob/main/Backup/ModelLoad_Analyse.java) 
This has a vivid description, explaining the workflow of [ModelLoad](https://github.com/virtualcell/vcell/blob/master/vcell-imagej-helper/src/main/java/org/vcell/imagej/plugin/ModelLoad.java) 

#### [VCellPlugin_JComboBox](https://github.com/nikitamahoviya/Plugins_VCell-ImageJ/blob/main/Backup/VCellPlugin_JComboBox.java) 
This is to understand how JComboBox is functioning inside [VCellPlugin](https://github.com/virtualcell/vcell/blob/master/vcell-imagej-helper/src/main/java/org/vcell/imagej/plugin/VCellPlugin.java]). The original file was having the JComboBox commands as comments so on uncommenting them we can see how those commands are responding.

#### [Combinefig](https://github.com/nikitamahoviya/Plugins_VCell-ImageJ/blob/main/Backup/Combinefig.java) 
This is the initiation of the plug-in development of groovy script [combineFig.groovy](https://github.com/virtualcell/vcell/blob/master/vcell-imagej-helper/combineFig.groovy) 

## Testing of Plugin in Various Cases

Reference: [VCellPlugin.java](https://github.com/virtualcell/vcell/blob/master/vcell-imagej-helper/src/main/java/org/vcell/imagej/plugin/VCellPlugin.java)

### Testing A:

 - Did Not log in to my VCell Account
 - Activated my Fiji ImageJ service
 
 Which gave the resultant as

![image](https://user-images.githubusercontent.com/43717626/124156564-68f8be80-dab5-11eb-97cd-150fba74c6db.png)

### Testing B:
 - Logged into my VCell Account
 - Stopped the Fiji (ImageJ) service

Which gave the resultant as

![image](https://user-images.githubusercontent.com/43717626/124158009-099bae00-dab7-11eb-9784-30dda620335b.png) 

## Future Scope

The project still has wide scopes for future developers. There are many other Groovy scripts that could be converted into a plugin referring to the other build plugins. Currently, some of the Groovy scripts might be broken because of outdated libraries or user access but they can be easily fixed with some meetings with the project mentors.

Groovy scripts being more powerful and flexible can be taken into use instead of plugins In the menu structure of plugins, an interface that is Macros can be added for reading the Groovy commands for various models and simulation values and producing results accordingly.

## Contribution Later

For contributions and improvements do refer to the [README](https://github.com/virtualcell/vcell#readme)  for the initial setup of VCell in your development environment and refer to [vcell-imagej-helper](https://github.com/virtualcell/vcell/tree/master/vcell-imagej-helper) for the plugin development.

## Acknowledgment

It had been a great experience working with all my mentors who guided and supported me in all the thicks and thins of the project. They have always given me time to explore and analyze things from various perspectives. They have helped me tremendously to gather and analyze my data, as well as interpreting the results and communicating these results. They were instrumental to my work and I am lucky to have such welcoming mentors.

## Conclusion

The coding period of 10 weeks had been amazing as it gave me exposure to real-life problems. All thanks to the open-source community and NRNB for providing an environment that helped me enhance my skills and be beyond that.

For any other information about the project, we can always get connected over at 
- [LinkedIn](https://www.linkedin.com/in/nikita-mahoviya-28034b171/) 
- [Github](https://github.com/nikitamahoviya) 
