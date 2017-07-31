# How to decompile odex to java

## odex to smali
```
baksmali deodex oat/ARM64/BLOB.odex -b framework/arm64/BOOT.oat
```
Replace BLOB.odex with the desired odex file, and BOOT.oat with the oat file that you think contains the odex file.

## smali to dex
```
smali assemble out -o classes.dex
```
"out" refers to the out folder created in the previous step.

## dex to jar
```
d2j-dex2jar.sh classes.dex
```
A classes-dex2jar.jar should be produced.

## View deodexed jar in JD-GUI
Just launch JD-GUI and open classes-dex2jar.jar.
Save all source files somewhere.


-------

*boot-oneplus_sdk_utils.oat*
* embryo