# Connections Installation Package based on *IBM Installation Manager (IIM)*

Build steps are common among all the Connections projects that uses Mantis,
however these needs extra attentions:

 - Development needs to be done on Windows, there are some compile errors
   on Linux
 - PackageDeveloper is needed for build installable packages for
   IBM Installation Manager

## Prepare the build environment

**`Z:` drive** - map to `\\mlsa2.cnx.cwp.pnp-hcl.com\aws-hcl-cwp-hawkins-mlsa2`

    C:\>net use
    New connections will be remembered.


    Status       Local     Remote                    Network

    -------------------------------------------------------------------------------
    OK           Z:        \\mlsa2.cnx.cwp.pnp-hcl.com\aws-hcl-cwp-hawkins-mlsa2
                                                Microsoft Windows Network
    The command completed successfully.

**lwp04.tools** - Download from following location and extract as `C:\work\lwp04.tools`:

    Z:\workplace\dailybuilds\TLS7.2\TLS7.2_20201117-1650\dist\TLS7.2.zip

**Eclipse 3.4.2** - Download and put to: `C:\work\eclipse`.

**PackageDeveloper** - Shall be available at: `C:\Program Files\IBM\PackageDeveloper162_i8`

**`C:\wplclocal.bat`** - Shall looks like:

```bat
set BUILD_HOME=C:\work\install-im
set FE_DOWNLOAD_DIR=\\mlsa2.cnx.cwp.pnp-hcl.com\aws-hcl-cwp-hawkins-mlsa2\workplace\dailybuilds,\\mlsa2.cnx.cwp.pnp-hcl.com\aws-hcl-cwp-hawkins-mlsa2\workplace\goldbuilds,\\mlsa2.cnx.cwp.pnp-hcl.com\aws-hcl-cwp-hawkins-mlsa2\workplace\goldkits
set BUILD_TOOLS=C:\work\lwp04.tools\lwp
set ANT_HOME=%BUILD_TOOLS%/mantis

set JAVA_HOME=%BUILD_TOOLS%/Java60
set ECLIPSE_HOME=C:\work\eclipse
set ALT_TOOLS=\\mlsa2\aws-hcl-cwp-hawkins-mlsa2\workplace\WPLCTools\
set JAVA_TOOL_OPTIONS=-Dfile.encoding=ISO-8859-1
```

## Build
Here are the steps to build the install pacakge:

1. Checkout out source code:
    ```bat
    git clone https://git.cwp.pnp-hcl.com/ic/install-im.git c:\work\install-im
    ```

1. Initialize build enviroment:
    ```bat
    C:\> cd C:\work\install-im
    C:\work\install\im\> C:\work\lwp04.tools\lwp\boot.sh
    ```

1. Double check the dependencies info in `install-im\sn.install\felement.xml`,
   ensure the dependencies are coming from correct build streams. For example,
   if working on a `IC7.0` branch, all the dependencies should be coming from
   `IC7.0_x` build streams instead of `IC10.0_` streams.

1. Download Dependencies:
    ```bat
    C:\work\install-im\> cd sn.install\lwp
    C:\work\install-im\sn.install\lwp\> wsbld downloadFEs
    ```

1. Run build:
    ```bat
    C:\work\install-im\sn.install\lwp\> wsbld
    C:\work\install-im\sn.install.lwp\> cd im.installer
    C:\work\install-im\sn.install.lwp\im.installer> wsbld
    ```
    Check the output of `wsbld` command carefully, the "`BUILD SUCCESSFUL`" message
    may not really mean the build is indeed successfully done.

1. Check build result, make sure the `setup` directory has the right contents:
    ```txt
      c:\work\install-im>dir setup
      Volume in drive C has no label.
      Volume Serial Number is 20B9-8648

      Directory of c:\work\install-im\setup

      12/10/2020  08:24 PM    <DIR>          .
      12/10/2020  08:24 PM    <DIR>          ..
      12/10/2020  08:40 PM    <DIR>          HCL_Connections_Install
                     0 File(s)              0 bytes
                     3 Dir(s)  68,999,348,224 bytes free

      c:\work\install-im>dir setup\HCL_Connections_Install
      Volume in drive C has no label.
      Volume Serial Number is 20B9-8648

      Directory of c:\work\install-im\setup\HCL_Connections_Install

      12/10/2020  08:40 PM    <DIR>          .
      12/10/2020  08:40 PM    <DIR>          ..
      12/10/2020  08:25 PM               995 autorun.inf
      12/10/2020  08:44 PM    <DIR>          HCLConnections
      12/10/2020  08:40 PM    <DIR>          IM
      12/10/2020  08:25 PM    <DIR>          launchpad
      12/10/2020  08:25 PM    <DIR>          Launchpad.app
      12/10/2020  08:25 PM           184,320 launchpad.exe
      12/10/2020  08:25 PM             1,258 launchpad.ini
      12/10/2020  08:25 PM             6,081 launchpad.sh
      12/10/2020  08:25 PM           208,896 launchpad64.exe
      12/10/2020  08:25 PM             1,263 launchpad64.ini
      12/10/2020  08:40 PM    <DIR>          tools
                     6 File(s)        402,813 bytes
                     7 Dir(s)  68,999,348,224 bytes free

      c:\work\install-im>dir setup\HCL_Connections_Install\HCLConnections
      Volume in drive C has no label.
      Volume Serial Number is 20B9-8648

      Directory of c:\work\install-im\setup\HCL_Connections_Install\HCLConnections

      12/10/2020  08:44 PM    <DIR>          .
      12/10/2020  08:44 PM    <DIR>          ..
      12/10/2020  08:41 PM    <DIR>          atoc
      12/10/2020  08:43 PM    <DIR>          files
      12/10/2020  08:44 PM    <DIR>          native
      12/10/2020  08:44 PM    <DIR>          Offerings
      12/10/2020  08:44 PM    <DIR>          plugins
      12/10/2020  08:41 PM                77 repository.config
      12/10/2020  08:44 PM             2,168 repository.xml
                     2 File(s)          2,245 bytes
                     7 Dir(s)  68,999,348,224 bytes free

    ```

### Troubleshooting the build failures

- **`build.properties`** file contains important properties and wrong value
can cause build failures.
  - `PD_HOME` - make sure PackageDeveloper is available at the specified location
  - `stellent.binaries` - make DCS is accessible at the location specified

- **`felement.xml`**, ensure the dependencies and their build streams listed in
  this file are correct, otherwise either the `wsbld downloadFEs` command will
  faile, or it may download wrong version of the dependencies


## Testing

Launch IBM Installation Manager, and pick "File" - "Preferences..." menu.
In the "Preferences" dialog box, pick "Repositories" from the left hand navigator.
If "C:\work\install-im\setup\HCL_Connections_Install\HCLConnections\repository.config"
is not listed, click "Add Repository..." button to add it. Click "OK" to close the
dialog box. Then the IIM can be used to do: *Install*, *Update*, *Modify*,
*Uninstall*, *Rollback*.

Test throughly before submit code change.

