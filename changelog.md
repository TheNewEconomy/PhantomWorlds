# Changelogs

## 2.1.0

### Major Changes
- Added Folia Support

### Minor Changes
- Internally moved all teleportation calls to a utility method, which will call it async if possible, otherwise sync
- Implemented a temporary cache for last player location per world, note this doesn't persist on restarts
- Removed custom environment option as it caused more confusion than it was worth
- Added serverProperties as a commandline argument for servers that have server.properties in a different location, add: -DserverProperties=location here(config/server.properties as example) to your server startup script.
- 
### Fixes
- Fixed issues with respawns after death
- Fixed issue with gamemode/effects set on spawn in