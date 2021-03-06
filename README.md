INSP NOTES FEB 2022: 
This is qt v5.4.3, but we might be able to sync the upstream layer from https://github.com/meta-qt5/meta-qt5 which seems to support 5.10.1

This layer depends on:

URI: git://github.com/openembedded/oe-core.git
branch: master
revision: HEAD

URI: git://github.com/openembedded/meta-oe.git
layers: meta-ruby
branch: master
revision: HEAD

When building stuff like qtdeclarative, qtquick, qtwebkit, make sure that
you have required PACKAGECONFIG options enabled in qtbase build, see qtbase.inc
for detail.

Send pull requests to openembedded-devel@lists.openembedded.org with '[meta-qt5]' in the subject'

When sending single patches, please using something like:
'git send-email -M -1 --to openembedded-devel@lists.openembedded.org --subject-prefix=meta-qt5][PATCH'

You are encouraged to fork the mirror on github[1] to share your
patches. This is preferred for patch sets consisting of more than one
patch. Other services like gitorious, repo.or.cz or self hosted setups
are of course accepted as well, 'git fetch <remote>' works the same on
all of them. We recommend github because it is free, easy to use, has
been proven to be reliable and has a really good web GUI.

1. https://github.com/meta-qt5/meta-qt5/

Main layer maintainers:
  Martin 'JaMa' Jansa <martin.jansa@gmail.com>
  Otavio Salvador <otavio@ossystems.com.br>
