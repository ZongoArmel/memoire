#!/usr/bin/env python3
"""Run pure Java data checks with a supplied org.json jar (20240303)."""
import argparse,os,pathlib,subprocess,tempfile
p=argparse.ArgumentParser();p.add_argument('--jdk',required=True);p.add_argument('--json-jar',required=True);a=p.parse_args()
r=pathlib.Path(__file__).resolve().parent;jdk=pathlib.Path(a.jdk).resolve();jar=str(pathlib.Path(a.json_jar).resolve());env=os.environ.copy();env['LD_LIBRARY_PATH']=str(jdk/'lib')+':'+str(jdk/'lib/server')+':'+env.get('LD_LIBRARY_PATH','')
names=['ArchiveValidation','SecureArchive','SearchEngine','BackupArchive','ProjectCatalog','RelationLinks','SearchPreset']
with tempfile.TemporaryDirectory(dir=r) as tmp:
 env['JAVA_TOOL_OPTIONS']='-Djava.io.tmpdir='+tmp
 sources=[str(r/'app/src/main/java/bf/memoire'/f'{n}.java') for n in names];tests=sorted((r/'tests').glob('*Test.java'))
 subprocess.run([str(jdk/'bin/java'),'com.sun.tools.javac.Main','-cp',jar,'-d',tmp,*sources,*map(str,tests)],check=True,env=env)
 for t in tests:subprocess.run([str(jdk/'bin/java'),'-cp',tmp+os.pathsep+jar,'bf.memoire.'+t.stem],check=True,env=env)
