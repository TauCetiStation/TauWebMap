def wcRed = pick(['#aa0808', '#990707', '#e50909', '#e50909'])
def wcBar = pick(['#0d8395', '#58b5c3', '#58c366', '#90d79a', '#3399ff', '#00ffff', '#ff6600', '#ffffff'])
def wcPurple = pick(['#ba62b1', '#Ba3fad', '#a54f9e', '#b549d1'])
def wcBrown = pick(['#9e5312', '#99761e', '#a56b00', '#d87f2b'])
def wcGreen = pick(['#aed18b', '#7bce23', '#5a9619', '#709348'])
def wcBlue = pick(['#8dbdd7', '#299bd8', '#1e719e', '#2bb8ff'])

def wcRedList = [['/area/station/security/armoury', '/area/station/security/brig', '/area/station/security/detectives_office',
                  '/area/station/security/hos', '/area/station/security/lobby', '/area/station/security/main', '/area/station/security/prison',
                  '/area/station/security/warden', '/area/station/security/range', '/area/station/security/forensic_office', '/area/station/security/checkpoint'
                 ]: wcRed]
def wcPurpleList = [['/area/station/rnd/lab', '/area/crew_quarters/hor', '/area/station/rnd/hallway', '/area/station/rnd/xenobiology', '/area/station/rnd/storage',
                     '/area/station/rnd/test_area', '/area/station/rnd/mixing', '/area/station/rnd/misc_lab', '/area/station/rnd/telesci', '/area/station/rnd/scibreak',
                     '/area/station/rnd/server', '/area/station/rnd/chargebay', '/area/station/rnd/robotics', '/area/station/rnd/brainstorm_center',
                     '/area/asteroid/research_outpost/hallway', '/area/asteroid/research_outpost/gearstore', '/area/asteroid/research_outpost/maint', '/area/asteroid/research_outpost/iso1',
                     '/area/asteroid/research_outpost/iso2', '/area/asteroid/research_outpost/harvesting', '/area/asteroid/research_outpost/outpost_misc_lab',
                     '/area/asteroid/research_outpost/anomaly', '/area/asteroid/research_outpost/med', '/area/asteroid/research_outpost/entry', '/area/asteroid/research_outpost/longtermstorage',
                     '/area/asteroid/research_outpost/tempstorage', '/area/asteroid/research_outpost/maintstore2'
                    ]: wcPurple]
def wcBrownList = [['/area/station/cargo/office', '/area/station/cargo/storage', '/area/station/cargo/qm',
                    '/area/station/cargo/recycler', '/area/station/cargo/recycleroffice', '/area/station/cargo/miningbreaktime',
                    '/area/station/cargo/miningoffice', '/area/asteroid/mine/production', '/area/asteroid/mine/eva',
                    '/area/asteroid/mine/living_quarters', '/area/asteroid/mine/maintenance', '/area/asteroid/mine/west_outpost'
                   ]: wcBrown]
def wcGreenList = [['/area/station/medical/psych', '/area/station/medical/patients_rooms', '/area/station/medical/patient_a', '/area/station/medical/patient_b',
                    '/area/station/medical/medbreak', '/area/station/medical/surgeryobs', '/area/station/medical/surgery', '/area/station/medical/surgery2',
                    '/area/station/medical/hallway/outbranch', '/area/station/medical/virology', '/area/hydroponics',
                    '/area/asteroid/research_outpost/maintstore1', '/area/asteroid/research_outpost/sample'
                   ]: wcGreen]
def wcBlueList = [['/area/station/medical/reception', '/area/station/medical/morgue', '/area/station/medical/hallway',
                   '/area/station/medical/genetics_cloning', '/area/station/medical/genetics', '/area/station/medical/cmo'
                  ]: wcBlue]
def wcBarList = [['/area/station/civilian/bar']: wcBar]

setGlobal('areaColorsLists', [wcRedList, wcPurpleList, wcBrownList, wcGreenList, wcBlueList, wcBarList])
