# Add any ProGuard configurations specific to this
# extension here.

-keep public class com.oseamiya.unityads.UnityAds {
    public *;
 }
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively

-repackageclasses 'com/oseamiya/unityads/repack'
-flattenpackagehierarchy
-dontpreverify
