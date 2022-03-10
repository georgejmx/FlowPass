# FlowPass

Native Android password manager app. Uses encryption to securely store data on device

## Build and Test

To build the project, clone the repo then open Android studio. Next;
1. Navigate to the project *build.gradle* file, then press **Sync**. This loads
in all dependencies to your system
2. Either connect to a physical Android device or install an AVD emulator
3. Hit *CTRL + F10* to build and run the project

## Security Design

*add here*

## Roadmap

### v0.4

- ~~backup feature to save the db and restore~~
- ~~order main entry view alphabetically~~

### v0.4.1

- remove null values that can appear
- update prompts, using new font


### v0.5

- Integrate with flow-server to store all backups online
	- start with hardcoded username and password; retreiving then 
	uploading backups
	- add in user validation
- add landing page, where registration or backup buttons appear;
should only load once
- style input boxes better


### v1.0

- Add seed phrase into key generation
- UI revamp
	- maximum two explicit FABs per page
- release .APK
