DESCRIPTION = "U-Boot - the Universal Boot Loader"
HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"
SECTION = "bootloaders"
PRIORITY = "optional"
LICENSE = "GPL"
PROVIDES = "virtual/bootloader"

PACKAGE_ARCH = "${MACHINE_ARCH}"
PARALLEL_MAKE=""

EXTRA_OEMAKE = "CROSS_COMPILE=${TARGET_PREFIX}"

UBOOT_MACHINE ?= "${MACHINE}_config"
UBOOT_IMAGE ?= "u-boot-${MACHINE}-${PV}-${PR}.bin"
UBOOT_SYMLINK ?= "u-boot-${MACHINE}.bin"

do_compile () {
	unset LDFLAGS
	unset CFLAGS
	unset CPPFLAGS
	oe_runmake ${UBOOT_MACHINE}
	oe_runmake all
	oe_runmake tools
}

do_install () {
	if [ -e ${WORKDIR}/fw_env.config ] ; then
	    install -d ${D}$base_sbindir}
		install -d ${D}${sysconfdir}
		install -m 644 ${WORKDIR}/fw_env.config ${D}${sysconfdir}/fw_env.config
		install -m 755 ${S}/tools/env/fw_printenv ${D}${base_sbindir}/fw_printenv
		install -m 755 ${S}/tools/env/fw_printenv ${D}${base_sbindir}/fw_setenv
	fi
}

do_deploy () {
	install -d ${DEPLOY_DIR_IMAGE}
	install ${S}/u-boot.bin ${DEPLOY_DIR_IMAGE}/${UBOOT_IMAGE}
	package_stagefile_shell ${DEPLOY_DIR_IMAGE}/${UBOOT_IMAGE}

	cd ${DEPLOY_DIR_IMAGE}
	rm -f ${UBOOT_SYMLINK}
	ln -sf ${UBOOT_IMAGE} ${UBOOT_SYMLINK}
	package_stagefile_shell ${DEPLOY_DIR_IMAGE}/${UBOOT_SYMLINK}
}
do_deploy[dirs] = "${S}"
addtask deploy before do_build after do_compile

do_stage() {
	install -d ${STAGING_BINDIR_NATIVE}
	install -m 755 tools/mkimage ${STAGING_BINDIR_NATIVE}/
}
