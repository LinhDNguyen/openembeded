DESCRIPTION = "DSPLINK 1.61.3 module for TI ARM/DSP processors"

require ti-paths.inc
inherit module

# compile and run time dependencies
DEPENDS 	+= "virtual/kernel perl-native ti-dspbios-native ti-cgt6x-native update-modules ti-xdctools-native"

# tconf from xdctools dislikes '.' in pwd :/
#This is a kernel module, don't set PR directly
MACHINE_KERNEL_PR_append = "a"                                                  
PV = "1613"

SRC_URI = "http://install.source.dir.local/dsplink_1_61_03.tar.gz \
		   file://loadmodules-ti-dsplink-apps.sh \
		   file://unloadmodules-ti-dsplink-apps.sh"

# Set the source directory
S = "${WORKDIR}/dsplink_1_61_03"
	
# DSPLINK - Config Variable for different platform
DSPLINKPLATFORM            			?= "DAVINCI"
DSPLINKPLATFORM_dm6446-evm 			?= "DAVINCI"
DSPLINKPLATFORM_da830-omapl137-evm 	?= "OMAPL1XX"

DSPLINKDSPCFG            			?= "DM6446GEMSHMEM"
DSPLINKDSPCFG_dm6446-evm 			?= "DM6446GEMSHMEM"
DSPLINKDSPCFG_da830-omapl137-evm 	?= "OMAPL1XXGEMSHMEM"

DSPLINKGPPOS             			?= "MVL5G"
DSPLINKGPPOS_dm6446-evm  			?= "MVL5G"
DSPLINKGPPOS_da830-omapl137-evm  	?= "MVL5G"

DSPLINK = "${S}/dsplink"
export DSPLINK

STAGING_TI_DSPBIOS_DIR="${STAGING_DIR_NATIVE}/ti-dspbios-native"
STAGING_TI_CGT6x_DIR="${STAGING_DIR_NATIVE}/ti-cgt6x-native"
STAGING_TI_XDCTOOL_INSTALL_DIR="${STAGING_DIR_NATIVE}/ti-xdctools-native"

do_compile() {

    # Run perl script to create appropriate makefiles (v1.60 and up)
    (
    cd ${DSPLINK}
    perl config/bin/dsplinkcfg.pl --platform=${DSPLINKPLATFORM} --nodsp=1 \
	--dspcfg_0=${DSPLINKDSPCFG} --dspos_0=DSPBIOS5XX \
	--gppos=${DSPLINKGPPOS} --comps=ponslrm
    )

	  # dsplink makefile is hard-coded to use kbuild only on OMAP3530.
    # we are forcing  to use kbuild on other platforms.
	  sed -i  's/OMAP3530/${DSPLINKPLATFORM}/g' ${DSPLINK}/gpp/src/Makefile	

    # TODO :: KERNEL_CC, etc need replacing with user CC
    # TODO :: Need to understand why OBJDUMP is required for kernel module
    # Unset these since LDFLAGS gets picked up and used incorrectly.... need 
    # investigation

    unset CFLAGS CPPFLAGS CXXFLAGS  LDFLAGS
    
    # Build the gpp user space library
    cd ${DSPLINK}/gpp/src/api
    ${STAGING_TI_XDCTOOL_INSTALL_DIR}/gmake \
      CROSS_COMPILE="${TARGET_PREFIX}" \
      CC="${KERNEL_CC}" \
      AR="${KERNEL_AR}" \
      LD="${KERNEL_LD}" \
      COMPILER="${KERNEL_CC}" \
      ARCHIVER="${KERNEL_AR}" \
      KERNEL_DIR="${STAGING_KERNEL_DIR}" \
      clean all

    # Build the gpp kernel space (debug and release)
    cd ${DSPLINK}/gpp/src
    ${STAGING_TI_XDCTOOL_INSTALL_DIR}/gmake \
      OBJDUMP="${TARGET_PREFIX}objdump" \
      CROSS_COMPILE="${TARGET_PREFIX}" \
      CC="${KERNEL_CC}" \
      AR="${KERNEL_AR}" \
      LD="${KERNEL_LD}" \
      COMPILER="${KERNEL_CC}" \
      ARCHIVER="${KERNEL_AR}" \
      KERNEL_DIR="${STAGING_KERNEL_DIR}" \
      clean all

    # Build the gpp samples
    cd ${DSPLINK}/gpp/src/samples
    ${STAGING_TI_XDCTOOL_INSTALL_DIR}/gmake \
      BASE_TOOLCHAIN="${CROSS_DIR}" \
      BASE_CGTOOLS="${BASE_TOOLCHAIN}/bin" \
      OSINC_PLATFORM="${CROSS_DIR}/lib/gcc/${TARGET_SYS}/$(${TARGET_PREFIX}gcc -dumpversion)/include" \
      OSINC_TARGET="${BASE_TOOLCHAIN}/target/usr/include" \
      CROSS_COMPILE="${TARGET_PREFIX}" \
      CC="${KERNEL_CC}" \
      AR="${KERNEL_AR}" \
      LD="${KERNEL_LD}" \
      COMPILER="${KERNEL_CC}" \
      LINKER="${KERNEL_CC}" \
      ARCHIVER="${KERNEL_AR}" \
      KERNEL_DIR="${STAGING_KERNEL_DIR}" \
      clean all

    # Build the dsp library (debug and release)
    cd ${DSPLINK}/dsp/src
    ${STAGING_TI_XDCTOOL_INSTALL_DIR}/gmake \
      BASE_CGTOOLS="${STAGING_TI_CGT6x_DIR}" \
      BASE_SABIOS="${STAGING_TI_DSPBIOS_DIR}" \
      clean all

    # Build the dsp samples (debug and release)
    cd ${DSPLINK}/dsp/src/samples
    ${STAGING_TI_XDCTOOL_INSTALL_DIR}/gmake \
      BASE_CGTOOLS="${STAGING_TI_CGT6x_DIR}" \
      BASE_SABIOS="${STAGING_TI_DSPBIOS_DIR}" \
      clean all
}

do_install () {
    # DSPLINK driver - kernel module
    install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/dsp
    install -m 0755 ${DSPLINK}/gpp/export/BIN/Linux/${DSPLINKPLATFORM}/RELEASE/dsplinkk.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/dsp/ 

    # DSPLINK library
    install -d ${D}/${installdir}/dsplink/libs
    install -m 0755 ${DSPLINK}/gpp/export/BIN/Linux/${DSPLINKPLATFORM}/RELEASE/dsplink.lib  ${D}/${installdir}/dsplink/libs

    # DSPLINK sample apps
    install -d ${D}/${installdir}/dsplink/apps
    
    cp ${DSPLINK}/gpp/export/BIN/Linux/${DSPLINKPLATFORM}/RELEASE/*gpp ${D}/${installdir}/dsplink/apps
    
    for i in $(find ${DSPLINK}/dsp/BUILD/ -name "*.out") ; do
        cp ${i} ${D}/${installdir}/dsplink/apps
    done

    # DSPLINK test app module un/load scripts
    install ${WORKDIR}/loadmodules-ti-dsplink-apps.sh ${D}/${installdir}/dsplink/apps
    install ${WORKDIR}/unloadmodules-ti-dsplink-apps.sh ${D}/${installdir}/dsplink/apps
}

do_stage () {
    install -d ${STAGING_DIR}/${MULTIMACH_TARGET_SYS}/${PN}/packages
    cp -pPrf ${S}/* ${STAGING_DIR}/${MULTIMACH_TARGET_SYS}/${PN}/packages
}

pkg_postrm () {
    update-modules || true
}

pkg_postinst () {
    if [ -n "$D" ]; then
        exit 1
    fi
    depmod -a
    update-modules || true
}

INHIBIT_PACKAGE_STRIP = "1"

PACKAGES += " ti-dsplink-apps" 
FILES_${PN} = "/lib/modules/${KERNEL_VERSION}/kernel/drivers/dsp/*"
FILES_ti-dsplink-apps = "${installdir}/dsplink/*"

# Disable QA check untils we figure out how to pass LDFLAGS in build
INSANE_SKIP_${PN} = True
INSANE_SKIP_ti-dsplink-apps = True

