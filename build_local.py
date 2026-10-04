#!/usr/bin/env python3
"""Build this dependency-free Android app with an installed SDK and JDK 17.
Usage: python3 build_local.py --sdk /path/to/sdk --jdk /path/to/jdk
"""
from pathlib import Path
import argparse, os, subprocess, zipfile, shutil
parser=argparse.ArgumentParser();parser.add_argument('--sdk',required=True);parser.add_argument('--jdk',required=True);args=parser.parse_args()
root=Path(__file__).resolve().parent;sdk=Path(args.sdk).resolve();jdk=Path(args.jdk).resolve();bt=sdk/'build-tools/35.0.0';jar=sdk/'platforms/android-35/android.jar';build=root/'build-local';build.mkdir(exist_ok=True);(build/'tmp').mkdir(exist_ok=True);(build/'classes').mkdir(exist_ok=True)
env=os.environ.copy();env['JAVA_HOME']=str(jdk);env['PATH']=str(jdk/'bin')+os.pathsep+env.get('PATH','');env['JAVA_TOOL_OPTIONS']='-Djava.io.tmpdir='+str(build/'tmp');env['LD_LIBRARY_PATH']=os.pathsep.join(map(str,[bt/'lib64',jdk/'lib',jdk/'lib/server']))+os.pathsep+env.get('LD_LIBRARY_PATH','')
def run(*cmd):subprocess.run(list(map(str,cmd)),check=True,env=env,cwd=root)
java=jdk/'bin/java'
manifest=(root/'app/src/main/AndroidManifest.xml').read_text().replace('<manifest xmlns:android=', '<manifest package="bf.memoire" android:versionCode="4" android:versionName="0.4.0" xmlns:android=').replace('<uses-permission','<uses-sdk android:minSdkVersion="26" android:targetSdkVersion="34"/><uses-permission',1);(build/'AndroidManifest.xml').write_text(manifest)
run(java,'com.sun.tools.javac.Main','-source','17','-target','17','-classpath',jar,'-d',build/'classes',*sorted((root/'app/src/main/java/bf/memoire').glob('*.java')))
run(bt/'aapt2','compile','--dir',root/'app/src/main/res','-o',build/'resources.zip')
run(bt/'aapt2','link','-o',build/'base.apk','--manifest',build/'AndroidManifest.xml','-I',jar,build/'resources.zip')
run(java,'-cp',bt/'lib/d8.jar','com.android.tools.r8.D8','--lib',jar,'--min-api','26','--output',build,*sorted((build/'classes').rglob('*.class')))
shutil.copyfile(build/'base.apk',build/'unsigned.apk')
with zipfile.ZipFile(build/'unsigned.apk','a') as z:z.write(build/'classes.dex','classes.dex')
run(bt/'zipalign','-f','4',build/'unsigned.apk',build/'aligned.apk')
key=root/'signing/memoire-dev.jks';key.parent.mkdir(exist_ok=True)
if not key.exists():run(jdk/'bin/keytool','-genkeypair','-keystore',key,'-storepass','android','-keypass','android','-alias','memoire','-dname','CN=Memoire Personal Development','-keyalg','RSA','-keysize','2048','-validity','10000')
output=root/'Memoire-0.4.0.apk'
run(java,'-jar',bt/'lib/apksigner.jar','sign','--ks',key,'--ks-pass','pass:android','--key-pass','pass:android','--out',output,build/'aligned.apk')
run(java,'-jar',bt/'lib/apksigner.jar','verify','--verbose',output)
run(bt/'zipalign','-c','4',output)
print('APK:',output)
