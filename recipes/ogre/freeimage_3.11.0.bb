LICENSE = "MIT"

PR = "r1"

SRC_URI = "${SOURCEFORGE_MIRROR}/freeimage/FreeImage3110.zip"

S = "${WORKDIR}/FreeImage/"

TARGET_CC_ARCH += "${LDFLAGS}"

do_configure() {
    sed -i -e /^CC/d \
           -e /^CXX\ /d \
           -e /^AR/d \
           -e /^INCDIR\ /d \
           -e /^INSTALLDIR\ /d \
           -e s:'-o root -g root'::g \
           -e /ldconfig/d \
    ${S}/Makefile.gnu
}

do_install() {
	install -d ${D}${libdir}
	install -d ${D}${includedir}
	oe_runmake INSTALLDIR="${D}${libdir}" INCDIR="${D}${includedir}" install
}

do_stage() {
	install -d ${STAGING_LIBDIR}
	install -d ${STAGING_INCDIR}
	oe_runmake  INSTALLDIR="${STAGING_LIBDIR}" INCDIR="${STAGING_INCDIR}" install
}

