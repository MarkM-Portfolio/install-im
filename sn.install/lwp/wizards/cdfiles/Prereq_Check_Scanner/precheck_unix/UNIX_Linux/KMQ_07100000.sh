#!/bin/sh
cd UNIX_Linux
set -x
. /root/prereqchecker/lib/common_function.sh
`wrlTrace Starting os.space`
`wrlTrace Executing os.space`
`wrlDebug Starting os.space`
`wrlDebug Expected 10MB `
ss=`./os.space usr`
`wrlTrace Finished os.space`
echo "os.space.usr=$ss"
`wrlDebug Finished os.space`
`wrlDebug OutPutValueIs $ss`
`wrlTrace Done os.space`
`wrlTrace Starting os.space`
`wrlTrace Executing os.space`
`wrlDebug Starting os.space`
`wrlDebug Expected 650MB `
ss=`./os.space tmp`
`wrlTrace Finished os.space`
echo "os.space.tmp=$ss"
`wrlDebug Finished os.space`
`wrlDebug OutPutValueIs $ss`
`wrlTrace Done os.space`
`wrlTrace Starting user.isAdmin`
`wrlTrace Executing user.isAdmin`
`wrlDebug Starting user.isAdmin`
`wrlDebug Expected True `
ss=`./os.user`
`wrlTrace Finished user.isAdmin`
echo "user.isAdmin=$ss"
`wrlDebug Finished user.isAdmin`
`wrlDebug OutPutValueIs $ss`
`wrlTrace Done user.isAdmin`
`wrlTrace Starting installedSoftware.WMQ.version`
`wrlTrace Executing installedSoftware.WMQ.version`
`wrlDebug Starting installedSoftware.WMQ.version`
`wrlDebug Expected 7.0* `
ss=`./installedSoftware.WMQ.version`
`wrlTrace Finished installedSoftware.WMQ.version`
echo "installedSoftware.WMQ.version=$ss"
`wrlDebug Finished installedSoftware.WMQ.version`
`wrlDebug OutPutValueIs $ss`
`wrlTrace Done installedSoftware.WMQ.version`
