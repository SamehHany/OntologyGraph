import sys
import os
import commands
from subprocess import call
import re

#in_path = 'property-queries/'
out_path = 'single-queries/'

if os.path.isdir(sys.argv[1]):
	files = os.listdir(sys.argv[1])
	files_full_path = []
	for i in range(len(files)):
		files_full_path.append(sys.argv[1] + '/' + files[i])
else:
	files = [sys.argv[1]]
	files_full_path = [sys.argv[1]]

pattern = re.compile("\[.*\]")
for file_full_path, fl in zip(files_full_path, files):
	fin = open(file_full_path, 'r')

	query = ''
	name = ''
	new_query = False
	
	inQuery = False
	for line in fin:
		line_s = line.strip()
		if pattern.match(line_s):
			inQuery = True
			line_s = line_s.strip('[]')
			name = line_s.split('=')[1].strip().strip('\"');
		elif line_s is not '' and ']]' not in line_s and inQuery:
			query = query + line
		elif line_s is '' and inQuery:
			filepath = out_path + fl[:-2] + '$' + name.replace(' ', '_').replace('(','#').replace(')','^') + '.q'
			fout = open(filepath, 'w') 
			fout.write(query + '\n')
			fout.close()
			query = ''
			inQuery = False

	fin.close()
