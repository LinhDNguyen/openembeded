DESCRIPTION = "Python examples for Opie"
SECTION = "opie/base"
PRIORITY = "optional"
LICENSE = "GPL"
RDEPENDS = "python-pyqt opie-pyquicklaunchapplet"
APPNAME = "python"
PACKAGE_ARCH = "all"

S = "${WORKDIR}/${APPNAME}"

inherit opie

do_configure() {
	:
}

do_compile() {
	:
}

do_install() {
        install -d ${D}${palmtopdir}/bin/python/ ${D}${palmtopdir}/apps/Python
	install -m 0755 *.py ${D}${palmtopdir}/bin/python/
	install -m 0644 ${WORKDIR}/apps/Python/.directory ${D}${palmtopdir}/apps/Python/
	install -m 0644 ${WORKDIR}/apps/Python/simple.desktop ${D}${palmtopdir}/apps/Python/
}

