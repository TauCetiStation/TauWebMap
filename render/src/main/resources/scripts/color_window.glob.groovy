def wcRed = pick(['#aa0808', '#990707', '#e50909', '#e50909'])
def wcBar = pick(['#0d8395', '#58b5c3', '#58c366', '#90d79a', '#3399ff', '#00ffff', '#ff6600', '#ffffff'])
def wcPurple = pick(['#ba62b1', '#Ba3fad', '#a54f9e', '#b549d1'])
def wcBrown = pick(['#9e5312', '#99761e', '#a56b00', '#d87f2b'])
def wcGreen = pick(['#aed18b', '#7bce23', '#5a9619', '#709348'])
def wcBlue = pick(['#8dbdd7', '#299bd8', '#1e719e', '#2bb8ff'])

def wcRedList = [['/area/security/armoury', '/area/security/brig', '/area/security/detectives_office',
                  '/area/security/hos', '/area/security/lobby', '/area/security/main', '/area/security/prison',
                  '/area/security/warden', '/area/security/range', '/area/security/forensic_office', '/area/security/checkpoint'
                 ]: wcRed]
def wcPurpleList = [['/area/rnd/lab', '/area/crew_quarters/hor', '/area/rnd/hallway', '/area/rnd/xenobiology', '/area/rnd/storage',
                     '/area/rnd/test_area', '/area/rnd/mixing', '/area/rnd/misc_lab', '/area/rnd/telesci', '/area/rnd/scibreak',
                     '/area/toxins/server', '/area/assembly/chargebay', '/area/assembly/robotics', '/area/toxins/brainstorm_center',
                     '/area/research_outpost/hallway', '/area/research_outpost/gearstore', '/area/research_outpost/maint', '/area/research_outpost/iso1',
                     '/area/research_outpost/iso2', '/area/research_outpost/harvesting', '/area/research_outpost/outpost_misc_lab',
                     '/area/research_outpost/anomaly', '/area/research_outpost/med', '/area/research_outpost/entry', '/area/research_outpost/longtermstorage',
                     '/area/research_outpost/tempstorage', '/area/research_outpost/maintstore2'
                    ]: wcPurple]
def wcBrownList = [['/area/quartermaster/office', '/area/quartermaster/storage', '/area/quartermaster/qm',
                    '/area/quartermaster/recycler', '/area/quartermaster/recycleroffice', '/area/quartermaster/miningbreaktime',
                    '/area/quartermaster/miningoffice', '/area/mine/production', '/area/mine/eva',
                    '/area/mine/living_quarters', '/area/mine/maintenance', '/area/mine/west_outpost'
                   ]: wcBrown]
def wcGreenList = [['/area/medical/psych', '/area/medical/patients_rooms', '/area/medical/patient_a', '/area/medical/patient_b',
                    '/area/medical/medbreak', '/area/medical/surgeryobs', '/area/medical/surgery', '/area/medical/surgery2',
                    '/area/medical/hallway/outbranch', '/area/medical/virology', '/area/hydroponics',
                    '/area/research_outpost/maintstore1', '/area/research_outpost/sample'
                   ]: wcGreen]
def wcBlueList = [['/area/medical/reception', '/area/medical/morgue', '/area/medical/hallway',
                   '/area/medical/genetics_cloning', '/area/medical/genetics', '/area/medical/cmo'
                  ]: wcBlue]
def wcBarList = [['/area/crew_quarters/bar']: wcBar]

setGlobal('areaColorsLists', [wcRedList, wcPurpleList, wcBrownList, wcGreenList, wcBlueList, wcBarList])
