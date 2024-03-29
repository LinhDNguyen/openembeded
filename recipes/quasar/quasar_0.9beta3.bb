DESCRIPTION = "Quasar mediaplayer"
HOMEPAGE = "http://katastrophos.net/andre/blog/software/quasar-media-player/"
AUTHOR = "Andre Beckedorf"
LICENSE = "GPL"
SECTION = "multimedia"
RDEPENDS = "mplayer"
PV = "0.8+0.9beta3"
S = "${WORKDIR}/v0.9_beta3"

SRC_URI = "http://katastrophos.net/zaurus/sources/quasar/quasar_0.9_beta3_sources.tar.bz2\
	   file://cross-compile.patch;patch=1"

inherit qmake qt3x11

QMAKE_PROFILES = "quasar-qt.pro"

do_install() {
        install -d ${D}${bindir}
        install -m 0755 quasar ${D}${bindir}
	install -d ${D}${datadir}/applications
	install -m 0755 ${S}/distro/skeletons/pdaXrom/usr/share/applications/quasar.desktop ${D}${datadir}/applications/
	install -d ${D}${datadir}/pixmaps
        install -m 0755 ${S}/distro/images/quasar.png ${D}${datadir}/pixmaps/
	install -d ${D}${datadir}/quasar/skins
        install -d ${D}${datadir}/quasar/skins/default
	install -m 0755 ${S}/distro/skins/default/* ${D}${datadir}/quasar/skins/default/
}

