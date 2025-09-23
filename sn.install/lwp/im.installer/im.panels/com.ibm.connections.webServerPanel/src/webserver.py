f = open(sys.argv[0],'w')
f.write(AdminTask.listServers('[-serverType WEB_SERVER]'))
f.close()

