Open Source Cash Register

Copyright (C) 2013 Bernhard Streit

This file is part of the Open Source Cash Register program.

Open Source Cash Register is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License 
as published by the Free Software Foundation, either version 3 of 
the License, or (at your option) any later version.

Open Source Cash Register is distributed in the hope that it will 
be useful, but WITHOUT ANY WARRANTY; without even the implied 
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
 
--> See oscr/licenses/gpl-3.txt for a copy of the GNU GPL.

-------------------------------------------------------------------------------

This is a Maven Multi Module project. You need Apache Maven to compile the 
sources; I use the version 3.0.4. 


We try to stick to the git flow
  http://nvie.com/posts/a-successful-git-branching-model/
and use the atlassian maven jgitflow plugin
  https://bitbucket.org/atlassian/maven-jgitflow-plugin
  

How to get this project into eclipse?
=====================================

1. I suggest to clone this project using git. Alternatively, you can download 
   the sources from github.

2. Import the main project (oscr) into an eclipse workspace using "Import" - 
   "Maven" - "Existing Maven Projects". 
   This will automatically import the sub modules and link them in eclipse as
   projects.

How to launch the application for the first time?
=================================================

In order to launch the software for the first time, you need to create and 
connect to a database. The quickest way to do this is using the H2 database in
the sandbox environment:

1. Simply run 
      oscr-starter-sandbox/launchfiles/LoadInitialDataApp (Sandbox).launch
   which will create a folder "oscr-starter-sandbox/sandbox" and create a 
   database inside of it. 

2. Then run "oscr-starter-sandbox/launchfiles/SwingStarter (Sandbox)" to start
   the app using the h2 database which was just created. 

-------------------------------------------------------------------------------

Contact information:
   Bernhard.Streit+OSCR@gmail.com

Source Code @ Github:
   https://github.com/BernhardBln/opensourcecashregister
   Git Clone url: git@github.com:BernhardBln/opensourcecashregister.git

