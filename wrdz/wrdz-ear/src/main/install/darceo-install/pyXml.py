import os, sys
import xml.etree.ElementTree as ET

def changeNode(fileName, nodeName, newValue):
  tree = ET.parse(fileName)
  root = tree.getroot()
  elem = root.find(".//"+nodeName)
  elem.text=newValue
  tree.write(fileName)

def changeNodeAttr(fileName, nodeName, attribName, newValue):
  tree = ET.parse(fileName)
  root = tree.getroot()
  elem = root.find(".//"+nodeName)
  elem.attrib[attribName]=newValue
  tree.write(fileName)

def reSaveFile(fileName):
  tree = ET.parse(fileName)
  tree.write(fileName)

def main():
  argc=len(sys.argv)
  #print "argc="+str(argc)+"["+str(sys.argv[0]) + "]"
  xmlFileName = sys.argv[1]
  if (argc==2):
    reSaveFile(xmlFileName)
    return
  nodeName = sys.argv[2]
  if (argc==4):
    #print nodeName
    newValue = sys.argv[3]
    changeNode(xmlFileName, nodeName, newValue)
  else:
    attribName=sys.argv[3]
    newValue=sys.argv[4]
    changeNodeAttr(xmlFileName, nodeName, attribName, newValue)
if __name__=="__main__":main()
