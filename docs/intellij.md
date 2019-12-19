Using IntelliJ in Plugin Development
====================================

https://stackoverflow.com/a/43195085/2692307

* Open Eclipse project in Intellij (not repo root, but the plugin project root)
* Open Project Structure (``Ctrl``+``Alt``+``Shift``+``S``), add 
  ``{eclipseinstallationroot}/plugins`` and S2 root (``{userdirectory}/.p2/pool/plugins`` 
  in my case)
* Install Bash Plguin in IntelliJ
* Run the Plugin in Eclipse, open the Debug View, rightclick on the entry, 
  click on ``Properties``, copy the field value for "Command Line" and paste into 
  a ``bat`` file. The file path may not contain spaces.
  Add ``cd {eclipseinstallationroot} &&`` to the script content.
* Create a new bash configuration with the script as target. Add "Build Project" as
  Before-Launch-Option.
