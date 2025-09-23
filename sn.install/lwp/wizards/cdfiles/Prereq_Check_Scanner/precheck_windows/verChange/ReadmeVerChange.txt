Version Changer Tool: To change the version & build details of all the .vbs, .bat & readme files under the PRS base directory."
		Developed by: Dilip Muthukurussimana

How to execute:
-----------------
Run vc.bat
	This will internally call verChange.vbs as below
		vc.bat verChange.vbs <build date> [<version>]
		where:
			<build date>	- Build Date in YYYYMMDD format - Required
			<version>		- Version in V.R.M (Version.Release.Maintanence levels) - Optional
								If version is not provided, it will take the existing v.r.m value from preq.vbs and add 1 to the 'm' section of it. ie, if 1.0.44, then ver will be 1.0.45
			
		For e.g: 
			cs verChange.vbs 20101209 1.0.44
			cs verChange.vbs 20101209