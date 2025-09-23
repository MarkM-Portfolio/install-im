#!/usr/bin/python
# ***************************************************************** 
#                                                                   
# IBM Confidential                                                  
#                                                                   
# OCO Source Materials                                              
#                                                                   
# Copyright IBM Corp. 2010, 2014                                    
#                                                                   
# The source code for this program is not published or otherwise    
# divested of its trade secrets, irrespective of what has been      
# deposited with the U.S. Copyright Office.                         
#                                                                   
# ***************************************************************** 

lcadmin='wasadmin'
cognosadmin='wasadmin'
cognosusers='gccadmins'

appName='Activities'
AdminApp.edit(appName, '[-MapRolesToUsers [["person" No Yes "" ""] ["everyone" Yes No "" ""] ["reader" No Yes "" ""] ["metrics-reader" No No "" '+cognosusers+'] 
["search-admin" No No '+lcadmin+' ""] ["widget-admin" No No '+lcadmin+' ""] ["admin" No No '+lcadmin+' ""] ]]')
print "Setting Roles and Users for Activities"
AdminConfig.save()

appName='Blogs'
AdminApp.edit(appName, '[-MapRolesToUsers [["person" No Yes "" ""] ["everyone" Yes No "" ""]
 ["metrics-reader" No No "" '+cognosusers+'] ["admin" No No "" ""] ["global-moderator" No No '+lcadmin+' ""] ["search-admin" No No '+lcadmin+' ""] 
 ["widget-admin" No No '+lcadmin+' ""] ["reader" No Yes "" ""]  ]]')
print "Setting Roles and Users for Blogs"
AdminConfig.save()


appName='Common'
AdminApp.edit(appName, '[-MapRolesToUsers [["person" No Yes "" ""] ["allAuthenticated" No Yes "" ""] ["admin" No No '+lcadmin+' ""] ["everyone" Yes No "" ""] ["metrics-report-run" No No '+cognosadmin+' ""] ["global-moderator" No No "" ""] ["mail-user" No No '+lcadmin+' ""] ["reader" No Yes "" ""]  ]]')
print "Setting Roles and Users for Common"
AdminConfig.save()

appName='Communities'
AdminApp.edit(appName, '[-MapRolesToUsers [["everyone" Yes No "" ""] ["reader" No Yes "" ""] ["person" No Yes "" ""] ["metrics-reader" No No "" '+cognosusers+'] ["community-creator" No Yes "" ""] ["community-metrics-run" No No '+cognosadmin+' ""] ["search-admin" No No '+lcadmin+' ""] ["global-moderator" No No '+lcadmin+' ""] ["admin" No No '+lcadmin+' ""] ["dsx-admin" No No '+lcadmin+' ""] ["widget-admin" No No '+lcadmin+' ""]  ]]')
print "Setting Roles and Users for Communities"
AdminConfig.save()

appName='Dogear'
AdminApp.edit(appName, '[-MapRolesToUsers [["person" No Yes "" ""] ["everyone" Yes No "" ""] ["reader" No Yes "" ""] ["metrics-reader" No No "" '+cognosusers+'] ["search-admin" No No '+lcadmin+' ""]  ]]')
print "Setting Roles and Users for Dogear"
AdminConfig.save()

appName='Files'
AdminApp.edit(appName, '[-MapRolesToUsers [["everyone" Yes No "" ""] ["person" No Yes "" ""] ["reader" No Yes "" ""] ["metrics-reader"  No No "" '+cognosusers+'] ["everyone-authenticated" No Yes "" ""] ["files-owner" No Yes "" ""] ["admin" No No '+lcadmin+' ""] ["search-admin" No No '+lcadmin+' ""] ["widget-admin" No No '+lcadmin+' ""] ["global-moderator" No No '+lcadmin+' ""] ["org-admin" No No "" ""]  ]]')
print "Setting Roles and Users for Files"
AdminConfig.save()

appName='Forums'
AdminApp.edit(appName, '[-MapRolesToUsers [["person" No Yes "" ""] ["metrics-reader" No No "" '+cognosusers+'] ["reader" No Yes "" ""] ["everyone" Yes No "" ""] ["discussThis-user" No Yes "" ""] ["search-admin" No No '+lcadmin+' ""] ["widget-admin" No No '+lcadmin+' ""] ["admin" No No '+lcadmin+' ""] ["global-moderator" No No '+lcadmin+' ""] ["search-public-admin" No No '+lcadmin+' ""]  ]]')
print "Setting Roles and Users for Forums"
AdminConfig.save()

appName='Homepage'
AdminApp.edit(appName, '[-MapRolesToUsers [["person" No Yes "" ""] ["everyone" Yes No "" ""] ["reader" No Yes "" ""] ["metrics-reader" No No "" '+cognosusers+'] ["admin" No No '+lcadmin+' ""]  ]]')
print "Setting Roles and Users for Homepage"
AdminConfig.save()

appName='Metrics'
AdminApp.edit(appName, '[-MapRolesToUsers [["everyone" Yes No "" ""] ["person" No Yes "" ""] ["reader" No Yes "" ""] ["everyone-authenticated" No Yes "" ""] ["community-metrics-run" No No "" '+cognosusers+'] ["admin" No No '+lcadmin+' ""] ["metrics-report-run" No No '+cognosadmin+' ""]  ]]')
print "Setting Roles and Users for Metrics"
AdminConfig.save()

appName='Mobile'
AdminApp.edit(appName, '[-MapRolesToUsers [["everyone" Yes No "" ""] ["reader" No Yes "" ""] ["person" No Yes "" ""]  ]]')
print "Setting Roles and Users for Mobile"
AdminConfig.save()

appName='Mobile Administration'
AdminApp.edit(appName, '[-MapRolesToUsers [["administrator" No No '+lcadmin+' ""] ["everyone" No Yes "" ""]  ]]')
print "Setting Roles and Users for Mobile Administration"
AdminConfig.save()

appName='Moderation'
AdminApp.edit(appName, '[-MapRolesToUsers [["reader" No No "" ""] ["everyone-authenticated" No Yes "" ""] ["person" No Yes "" ""] ["global-moderator" No No '+lcadmin+' ""]  ]]')
print "Setting Roles and Users for Moderation"
AdminConfig.save()

appName='News'
AdminApp.edit(appName, '[-MapRolesToUsers [["person" No Yes "" ""] ["everyone" Yes No "" ""] ["reader" No Yes "" ""] ["sharebox-reader" No Yes "" ""] ["metrics-reader" No No "" '+cognosusers+'] ["allAuthenticated" No Yes "" ""] ["admin" No No '+lcadmin+' ""] ["search-admin" No No '+lcadmin+' ""] ["widget-admin" No No '+lcadmin+' ""]  ]]')
print "Setting Roles and Users for News"
AdminConfig.save()

appName='Profiles'
AdminApp.edit(appName, '[-MapRolesToUsers [["everyone" Yes No "" ""] ["reader" No Yes "" ""] ["person" No Yes "" ""] ["allAuthenticated" No Yes "" ""] ["metrics-reader" No No "" '+cognosusers+'] ["admin" No No '+lcadmin+' ""] ["search-admin" No No '+lcadmin+' ""] ["dsx-admin" No No '+lcadmin+' ""]  ]]')
print "Setting Roles and Users for Profiles"
AdminConfig.save()

appName='Search'
AdminApp.edit(appName, '[-MapRolesToUsers [["person" No Yes "" ""] ["everyone" Yes No "" ""] ["reader" No Yes "" ""] ["metrics-reader" No No "" '+cognosusers+'] ["admin" No No '+lcadmin+' ""] ["search-admin" No No '+lcadmin+' ""] ["everyone-authenticated" No Yes "" ""]  ]]')
print "Setting Roles and Users for Search"
AdminConfig.save()

appName='WebSphereOauth20SP'
AdminApp.edit(appName, '[-MapRolesToUsers [["authenticated" No Yes "" ""] ["client manager" No No "" ""]  ]]')
print "Setting Roles and Users for WebSphereOauth20SP"
AdminConfig.save()

appName='WidgetContainer'
AdminApp.edit(appName, '[-MapRolesToUsers [["person" No Yes "" ""] ["allAuthenticated" No Yes "" ""] ["admin" No No '+lcadmin+' ""] ["everyone" Yes No "" ""] ["reader" No Yes "" ""] ["metrics-reader" No No "" '+cognosusers+'] ["global-moderator" No No '+lcadmin+' ""] ["mail-user" No No "" ""] ["trustedExternalApplication" No No '+lcadmin+' ""]  ]]')
print "Setting Roles and Users for WidgetContainer"
AdminConfig.save()

appName='Wikis'
AdminApp.edit(appName, '[-MapRolesToUsers [["everyone" Yes No "" ""] ["person" No Yes "" ""] ["reader" No Yes "" ""] ["metrics-reader" No No "" '+cognosusers+'] ["everyone-authenticated" No Yes "" ""] ["wiki-creator" No Yes "" ""] ["admin" No No '+lcadmin+' ""] ["search-admin" No No '+lcadmin+' ""] ["widget-admin" No No '+lcadmin+' ""]  ]]')
print "Setting Roles and Users for Wikis"
AdminConfig.save()
