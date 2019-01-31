setGlobal('contrabandPostersAmount', (env.getGlobalVar('contrabandposters') =~ /list\(/).size() - 1)
setGlobal('legitPostersAmount', (env.getGlobalVar('legitposters') =~ /list\(/).size() - 1)
