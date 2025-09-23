# ***************************************************************** 
#                                                                   
#                                                                   
# IBM Licensed Material                                             
#                                                                   
# Copyright IBM Corp. 2010, 2015                                           
#                                                                   
# The source code for this program is not published or otherwise    
# divested of its trade secrets, irrespective of what has been      
# deposited with the U.S. Copyright Office.                         
#                                                                   
# ***************************************************************** 

# 5724-S68                                                          
# 5724-S68                                                          
featureName=sys.argv[0]
print 'Populate login names for feature '+ featureName
if(featureName == 'activities'):
	execfile('activitiesAdmin.py')
	ActivitiesMigrationService.populateLoginNames()
elif(featureName == 'blogs'):
	execfile('blogsAdmin.py')
	BlogsMigrationService.populateLoginNames()
elif(featureName == 'communities'):
	execfile('communitiesAdmin.py')
	CommunitiesMigrationService.populateLoginNames()
elif(featureName == 'dogear'):
	execfile('dogearAdmin.py')
	DogearMigrationService.populateLoginNames()
elif(featureName == 'profiles'):
	execfile('profilesAdmin.py')
	ProfilesMigrationService.populateLoginNames()
elif(featureName == 'homepage'):
	execfile('homepageAdmin.py')
	HomepageMigrationService.populateLoginNames()
else:
	print 'Invalid feature name'+featureName
