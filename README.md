# ASL Sensor Test Suite

### Purpose

This program is used to analyze various aspects of seismic sensor data in order to determine information about their configuration, such as gain and orientation. It is meant to be a simple program that can be used to generate data on a wide range of sensor tests, a one-stop-shop for sensor diagnostics designed to replace several disparate programs across multiple languages. The program is designed around an interface meant to be simple and intuitive.

DOI: https://doi.org/10.5066/P9XXBOVR  
IPDS: IP-101116  

USGS Approved Release:  
Link: https://github.com/usgs/asl-sensor-suite/releases/tag/v1.4.2   
Commit Hash: f25cfd70afc2195ad1f651efb319264c285d4ef4  

### Requirements

##### Software
This program is designed to be used with Java 1.8. Some early releases were more compatible with 1.7 but changes to the date/time libraries used in recent releases will have made it incompatible.
This program also requires Gradle in order to be run from source. For instructions on installing Gradle, see https://gradle.org/install
Running the program with gradle can be done by using the command `gradle run`, which compiles the source into classes and runs the program that way; using the command `gradle build` produces an executable JAR file that can be launched using the command `java -jar build/libs/SensorTestSuite$version_number$.jar`.
NOTE: because Gradle requires access to the maven repositories to download dependencies, there may be build issues when running the program on DOI-networked computers. The instructions for using DOI certificates for maven authentication can be found under the Java subheader at https://github.com/usgs/best-practices/blob/master/WorkingWithinSSLIntercept.md. You will also need the file "DOIRootCA.crt" from the linked page near the top detailing how to configure git.
For those using Mac computers, the last step for trusting the certificate in Java may be slightly different. If installing the certificate using the instructions above fails, try using this command instead (NOTE: requires administrative access due to the use of the sudo command) https://blog.alwold.com/2011/06/30/how-to-trust-a-certificate-in-java-on-mac-os-x/

##### Hardware
Because SEED files must be decompressed and stored in memory, the footprint of this application has the potential to be rather large. Running this program on OSX v. 10.11.15, the total memory usage was 76.6 MB compressed but 1.82 GB uncompressed. As a result, this program's performance may be dependent on the memory management systems used by the OS of the system it runs on.

### Compilation

While releases are regularly updated with major feature changes included in the releases tab on this project's Github and approximately-daily snapshots included among the projects files, it may be desired to build the project direct from source. There are a few ways of doing this, explained below.

##### Command Line
The program can be compiled by using the commands `gradle compileJava` which will compile the source code, or `gradle build` which will also run the unit tests.
Running the program can be done by either opening the jar through a filebrowser or running either `gradle run`, which launches the jar file, or `java -jar build/libs/SensorTestSuite$version_number$.jar` after the program has been built, with $version_number$ replaced with the current version, based on the value of the parameter 'version' in the build.gradle file. The gradle build script also allows the built jar file to be placed in the root directory; if `gradle compileJava` was previously run, then `gradle copyJar` will move it there. Note that `gradle build` includes this step by default, and also runs all tests -- in the initial run of tests (also performed by `gradle test` or `gradle check`, this may be slow, as the test data used in the test cases will need to be downloaded before they can be run, though once this is completed, running the test cases will likely only take a couple minutes).

##### Eclipse
For those who wish to compile and run this program with Eclipse, run the command `gradle eclipse` and then, inside eclipse, go to File>"Open projects from file system..." and direct Eclipse to the root folder of the test suite. Now the code will be available as an Eclipse project. For more information on using Eclipse, consult the Eclipse documentation.

### File Selection
The program will default to looking for SEED files in the "data" subdirectory in the same folder the jar is, if it exists. It is possible to choose a different folder, which will become the new default folder when loading in additional files. It will not, however, persist after the program is closed.
SEED files must have an intersecting time range, which will be displayed when multiple files are loaded in. The bars below the input plots can be used to select a narrower range of data to zoom in on. Loading in a SEED file that does not have any common time range with the other data will produce an error; to reset the loaded SEED files, they can either be unloaded individually with a corresponding remove button for that data, or all data can be cleared out with the 'clear all' button.

Note that orange bars in the input plots indicate the existence of gaps in some of the input files, such as in the case of calibration sequences, which usually have gaps between each calibration operation. While this program does not prevent running calculations over regions of data which include gaps, it is advised to select regions of data which do not contain these gaps, as doing so can lead to undesirable behavior from the program and faulty results due to timing issues as a result of padding.

The program also comes with a number of response files (in RESP format) embedded in the jar, selectable from a drop-down menu, that correspond to several common sensor configurations. It is also possible to load in other response files from the 'load custom response' option in a manner similar to SEED files described above, the default directory being the 'responses' folder in the same folder as the jar file. Note that clearing a SEED file also clears out the corresponding response.

### Output

Plots of the input files used in a sensor test can be output in PNG, as can the output of a given sensor test, using the corresponding buttons in each panel. Both can be compiled into a single-page PDF of all currently displayed data using the button at the bottom of the program.

### Usage

As noted above, running the program can be done using `gradle run` if using a full source download or `java -jar [TestSuite jar filename]` with the name of the currently-built application replacing the bracketed bit. Note that the program may require an increase in the normal java heap space in order to run files with a very large number of data points. In such a case, running the program should be done with `java -Xmx#G -jar [TestSuite jar filename]` with `#` being replaced with the number of gigabytes to allocate to the Java heap space. It is unlikely that the program will require more than 4 gigabytes of memory.

The following describes what is required in order to run a specific calculation through the GUI. For more information on the specific details of certain tests or how to create code to automate testing, consult the javadoc (code documentation).

#### Self-noise

Self-noise requires three components and an appropriate response file for each. The test computes the cross-power (power-spectral density) of each pair of files, and uses that data to extract estimations of the inherent noise of each sensor. Plots of the seismic NLNM and NHNM are also included. Units of frequency (Hz) or period (seconds, default) can be selected using the checkmark in the bottom-left of the panel.

The input files do not need to be in any particular order. They all must have responses specified. For three-component self-noise, they should all be pointing in the same direction (i.e., all facing north).

There is also a nine-component self-noise test that takes in [horizontal] north, east, and vertical sensor data for each of the three components, finds the best angle to rotate the horizontal components to maximize coherence, and then performs the same test on the 3 sensors in each direction. 

#### Relative Gain

Relative gain computes the mean of the PSD of each of two sensors, and estimates the gain from the mean of the ratio of the values over a selected range of the output.

The input files, again, do not need to be in any order (the panel allows for choosing which sensor to be used as reference by way of the selection menus below the chart), but they must both have responses specified.

The gain is initially calculated using the octave around the peak frequency, but a custom range can be specified using the sliders, functioning similarly to the region selectors for input data.

There is also a six-component relative gain test that takes in horizontal north, east, and vertical sensor data, finds the coherence-maximizing angle, and then takes the statistics over the rotated components. The first set of data is always taken as the reference for horizontal rotation; the selection menu on the left side of this panel allows for setting either input as the gain reference.

#### Step Calibration

Step calibration takes in a step input signal and the response to that signal from a sensor. It attempts to find response parameters that best fit the application of that response to the input, or rather, the response that when deconvolved with the input's signal produces a function closest to the step.

The input files have a specific order: the step input signal must be placed first, though it does not use a response. The second input, then, is the output from the sensor of interest, and a response should be chosen to help guide the initial guess of the solver.

Note that for the graphical output, the red curve represents the normalized step function used as input, the blue curve represents the calculated step gotten from deconvolving the response from the sensor output, and the green curve represents the calculated step using the best-fit response parameters. A good fit should have the green curve lined up as closely with the red curve as possible.

#### Randomized calibration

This function solves for poles to attempt to fit the response curve calculated from deconvolving the given calibration input from the sensor output. Low-frequency (the two lowest poles) and high-frequency (all other poles) are fitted to minimize the difference between the estimated response, based on the response specified for the sensor. The inputs follow the same structure as step calculation, though what response parameters are solved for is dependent on whether a high or low frequency calculation is chosen. Both the magnitude and argument (angle of the response curve along the real axis) of the response curve are displayed in plots, and saving the plot to an image will include both such plots.

High-frequency calibrations have a tendency to become quite noisy close to the nyquist range. To ameliorate this issue, the program defaults to a max range of 80% of the nyquist rate for HF cal fits. This value can be changed from anywhere between 30% and 90%, but fits below 50% of Nyquist are not recommended and may produce errors in computation.

The program can calculate solutions to both resistive and capacitive calibrations according to the appropriate checkbox. This defaults to resistive calibrations.

When using embedded response files, it is strongly recommended to use an appropriate response file with "nocoil" in the name, as these remove the calibration coil's response from the file and thus generate more accurate results of calculations.

Note that plots have been scaled in order to produce more representative fits of response curves. This point occurs at 0.2 Hz for high-frequency cals (which is also the lowest-frequency point being fit to) and at 0.02 Hz for low-frequency cals.

Older high-frequency cals may produce lots of noise on the high-frequency end confounding the solver, especially depending on how the calibration was produced.
This program includes a second checker tab which does not run the solver for 
response parameters, but can be used to determine whether or not the calculated response from the sensor output is good enough to be used for the solver. 
Noisy calibrations or ones whose output otherwise varies significantly from the given nominal response may take a long time to solve or produce errors that lead to the solver being unable to converge on any solution.

The most common source of error is trying to solve for the wrong type of calibration compared to the input. Most high-frequency calibration data this program was tested with is sampled at 200Hz and is typically 15 minutes in length. Low-frequency calibrations may be around 10-20Hz but are about 8 hours long. Trying to run a high-frequency calibration on data lower than 40Hz will almost certainly produce an error, as may trying to run a low-frequency cal on data less than a few hours.

Note also that for the graphical output, the red curve is the response curve of the RESP loaded into the program, the blue curve is the calculated response of the sensor by deconvolving the calibration input from the sensor output, and the green curve is a response curve using the best-fit poles and zeros. A good fit is one in which the green curve lines up with the blue curve as much as possible.

#### Sine calibration

This panel is used to solve for the difference in amplitude between an input and output sine wave. Because the sine wave should have a fixed frequency, there is no response file used. This panel estimates amplitudes by calculating the sine wave RMS values and then scaled the input to the output by the ratio of those values. In addition, the program estimates the wavelength in order to give a rough estimate of the actual frequency of the calibration; it may be off by a few Hz in comparison to the expected frequency, though, and is mostly useful for guaranteeing that the calibration signal isn't being generated in a faulty way.

#### Azimuth

Azimuth takes in 3 inputs. The first two are orthogonal sensors assumed to be respectively facing near north and east. The third is a reference sensor assumed to point north, though the offset angle field can be used to specify a clockwise offset from north. The code will try to find a clockwise rotation angle that maximizes the coherence estimation between the rotated unknown-angle sensor data and the reference angle. This angle is added to the offset to produce the (clockwise) azimuth estimation. A value of the coherence estimations per-frequency for the found angle is also given as a separate plot. 

#### Orthogonality

Orthogonality takes in four inputs, two each from sensors known or assumed to be orthogonal, and finds the true (interior) angle between the second two sensors if the first two sensors are truly orthogonal.

The input files have a specific order: the first and third inputs are for north-facing sensors, and the second and fourth are for east-facing sensors. As noted above, the first two sensors are assumed to be 90 degrees apart for the purpose of the test; the second two sensors' orientation is what is solved for.

The output angles provided to get the estimate of the orientation of the test sensors may be in different quadrants from the expected value. This should not affect the quality of the estimate being provided by the solver for the angle between the sensors, however. If a more precise estimate of the angle is required, using the two known sensors as the test N and E in the azimuth panel, with the data under consideration as the reference sensor should provide the (inverse of) the actual orientation angle of the sensor in question.

#### Spectrum

This panel plots the PSD of 1-3 different sets of data, using both SEED and RESP data. Note that the data must have an intersecting time range in order to be loaded in. The data plotted in this panel is also produced during a self-noise test; this panel is most useful when wanting to get the power spectrum of one sensor in particular, as the relative gain and self-noise calculations can do the same for 2 or 3 sets of data respectively. 

#### Response

This plots 1-3 different response attenuation and phase curves (Bode plots) for given response files. The image generated from this plot will include both plots, though the program can only display one at a time (selectable with the drop-down menu in the bottom-left of the panel). Units of frequency (Hz, default) or period can be selected by the selection box on the bottom-right, much like with the self-noise plot.

This program also allows for extracting a response file embedded in the program, in order to be edited by hand. This way a field engineer with access to the program will be able to define a custom response file using the text editor of their choice. The response selected will be copied into the "responses" subdirectory with a date-stamped version of the same name as the embedded response file. (Copying the responses is done so that the nominal responses embedded in the program cannot be edited unwittingly. These new response files can be loaded in using the "load custom response" option with the response loader pane).

### Further Work / Known Issues

Currently the application does its best to show the complete range among all data, there are some issues in doing so. If there are three SEED files loaded and the first two SEED files have more data than the third, then when switching to a test using only two inputs, the entire range of the first two sensors should be visible. However, if there are loaded inputs not included in a test and a new file is loaded in one of the input slots, it must still have a time range in common with the unused inputs. While not ideal behavior, it prevents additional bugs from handling non-matching time ranges if a test using the non-active data is selected again.