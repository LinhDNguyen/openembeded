DESCRIPTION = "Linux Kernel for EM2440-III development board"
SECTION = "kernel"
LICENSE = "GPL"
PR = "r0"

GGSRC = "http://www.xora.org.uk/oe/patches/"

SRCREV = "${AUTOREV}"

#SRC_URI = "git://repo.or.cz/linux-2.6/mini2440.git;protocol=git;branch=mini2440-stable-v2.6.30 \
#           file://defconfig-mini2440"
SRC_URI = "git://github.com/nvl1109/kernel-twrk70f120m.git;protocol=git;branch=em2440-2.6.30 \
          file://defconfig-em2440iii"
S = "${WORKDIR}/git"
#S = "${WORKDIR}/linux-2.6.30.4"

KERNEL_IMAGETYPE = "uImage"
UBOOT_ENTRYPOINT = "30008000"

inherit kernel

COMPATIBLE_HOST = "arm.*-linux"
COMPATIBLE_MACHINE = "mini2440"

do_configure() {
	install ${WORKDIR}/defconfig-em2440iii ${S}/.config
	#oe_runmake oldconfig
	#oe_runmake
}

KERNEL_RELEASE = "2.6.30"
