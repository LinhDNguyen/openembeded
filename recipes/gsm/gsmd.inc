DESCRIPTION = "GSM libraries and daemons implementing the 07.10 specification"
HOMEPAGE = "http://www.openmoko.org"
LICENSE = "GPL LGPL"
SECTION = "libs/gsm"
PROVIDES += "gsmd"
RPROVIDES_${PN} = "libgsmd0 libgsmd gsmd gsmd-devel"
PV = "0.1+svnr${SRCREV}"
PR = "r44"

SRC_URI = "svn://svn.openmoko.org/trunk/src/target;module=gsm;proto=http \
           file://gsmd \
           file://default"
S = "${WORKDIR}/gsm"

inherit autotools pkgconfig update-rc.d
# handle update-rc.d RDEPENDS manually, we don't need it on
# anything but gsmd
RDEPENDS_append = ""

INITSCRIPT_NAME = "gsmd"
INITSCRIPT_PARAMS = "defaults 35"

do_stage() {
    autotools_stage_all
}

do_install_append() {
	install -d ${D}/${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/gsmd ${D}/${sysconfdir}/init.d/
	install -d ${D}/${sysconfdir}/default
	install ${WORKDIR}/default ${D}/${sysconfdir}/default/gsmd
}

PACKAGES =+ "\
  ${PN}-tools \
  ${BASEPN}-plugins \
  ${BASEPN}-plugin-machine-generic \
  ${BASEPN}-plugin-machine-tihtc \
  ${BASEPN}-plugin-machine-gta01 \
  ${BASEPN}-plugin-vendor-bcm \
  ${BASEPN}-plugin-vendor-qc \
  ${BASEPN}-plugin-vendor-ti \
  ${BASEPN}-plugin-vendor-tihtc \
"

ALLOW_EMPTY_${BASEPN}-plugin-machine-gta01 = "1"

RDEPENDS_${BASEPN}-plugins = "\
  ${BASEPN}-plugin-machine-generic \
  ${BASEPN}-plugin-machine-tihtc \
  ${BASEPN}-plugin-machine-gta01 \
  ${BASEPN}-plugin-vendor-bcm \
  ${BASEPN}-plugin-vendor-qc \
  ${BASEPN}-plugin-vendor-ti \
  ${BASEPN}-plugin-vendor-tihtc \
"

RDEPENDS_${PN} += "update-rc.d initscripts"
RRECOMMENDS_${PN} += "${BASEPN}-plugins"

FILES_${PN}-dbg += "${libdir}/gsmd/.debug/*"
FILES_${PN}-tools = "${bindir}/*"
FILES_${BASEPN}-plugins = ""
FILES_${BASEPN}-plugin-machine-generic = "${libdir}/gsmd/libgsmd-machine_generic.so*"
FILES_${BASEPN}-plugin-machine-tihtc = "${libdir}/gsmd/libgsmd-machine_tihtc.so*"
FILES_${BASEPN}-plugin-machine-gta01 = "${libdir}/gsmd/libgsmd-machine_gta01.so*"
FILES_${BASEPN}-plugin-vendor-qc = "${libdir}/gsmd/libgsmd-vendor_qc.so*"
FILES_${BASEPN}-plugin-vendor-bcm = "${libdir}/gsmd/libgsmd-vendor_bcm.so*"
FILES_${BASEPN}-plugin-vendor-ti = "${libdir}/gsmd/libgsmd-vendor_ti.so*"
FILES_${BASEPN}-plugin-vendor-tihtc = "${libdir}/gsmd/libgsmd-vendor_tihtc.so*"

PACKAGES_DYNAMIC = "lib${BASEPN}* ${BASEPN}"

ALLOW_EMPTY_${BASEPN}-plugins = "1"

RCONFLICTS_lib${BASEPN} = "lib${CONFLICTNAME}"
RCONFLICTS_${BASEPN} = "${CONFLICTNAME}"
RCONFLICTS_${BASEPN}-plugins = "${CONFLICTNAME}-plugins"
RCONFLICTS_${BASEPN}-plugin-machine-generic = "${CONFLICTNAME}-plugin-machine-generic"
RCONFLICTS_${BASEPN}-plugin-machine-tihtc = "${CONFLICTNAME}-plugin-machine-tihtc"
RCONFLICTS_${BASEPN}-plugin-machine-gta01 = "${CONFLICTNAME}-plugin-machine-gta01"
RCONFLICTS_${BASEPN}-plugin-vendor-qc = "${CONFLICTNAME}-plugin-vendor-qc"
RCONFLICTS_${BASEPN}-plugin-vendor-bcm = "${CONFLICTNAME}-plugin-vendor-bcm"
RCONFLICTS_${BASEPN}-plugin-vendor-ti = "${CONFLICTNAME}-plugin-vendor-ti"
RCONFLICTS_${BASEPN}-plugin-vendor-tihtc = "${CONFLICTNAME}-plugin-vendor-tihtc"

RPROVIDES_lib${BASEPN} += "lib${CONFLICTNAME}"
RPROVIDES_${BASEPN} = "${CONFLICTNAME}"
RPROVIDES_${BASEPN}-plugins = "${CONFLICTNAME}-plugins"
RPROVIDES_${BASEPN}-plugin-machine-generic = "${CONFLICTNAME}-plugin-machine-generic"
RPROVIDES_${BASEPN}-plugin-machine-tihtc = "${CONFLICTNAME}-plugin-machine-tihtc"
RPROVIDES_${BASEPN}-plugin-machine-gta01 = "${CONFLICTNAME}-plugin-machine-gta01"
RPROVIDES_${BASEPN}-plugin-vendor-qc = "${CONFLICTNAME}-plugin-vendor-qc"
RPROVIDES_${BASEPN}-plugin-vendor-bcm = "${CONFLICTNAME}-plugin-vendor-bcm"
RPROVIDES_${BASEPN}-plugin-vendor-ti = "${CONFLICTNAME}-plugin-vendor-ti"
RPROVIDES_${BASEPN}-plugin-vendor-tihtc = "${CONFLICTNAME}-plugin-vendor-tihtc"
