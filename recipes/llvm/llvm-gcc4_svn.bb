DESCRIPTION = "The Low Level Virtual Machine - gcc4 frontend"
HOMEPAGE = "http://llvm.org"
LICENSE = "various"

DEPENDS = "llvm-native"

PV = "2.0+svnr${SRCREV}"

inherit autotools cross

SRC_URI = "svn://anonsvn.opensource.apple.com/svn/llvm/;module=trunk \
	   "
	   
S = "${WORKDIR}/trunk"

EXTRA_OECONF = "--disable-shared \
                --enable-llvm \
	       "

