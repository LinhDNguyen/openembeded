DESCRIPTION = "Libcanberra is an implementation of the XDG Sound Theme and Name Specifications, for generating event sounds on free desktops."
LICENSE = "LGPL"
DEPENDS = "gtk+ pulseaudio alsa-lib"

inherit gconf autotools

SRC_URI = "http://0pointer.de/lennart/projects/libcanberra/libcanberra-${PV}.tar.gz"

EXTRA_OECONF = " --disable-oss " 
# This needs autoconf 2.62, which isn't used by any distro in OE atm
do_configure() {
	gnu-configize --force
	oe_runconf
}

FILES_${PN} += "${libdir}/gtk-2.0/modules/ ${datadir}/gnome"
FILES_${PN}-dbg += "${libdir}/gtk-2.0/modules/.debug"

AUTOTOOLS_STAGE_PKGCONFIG = "1"

do_stage() {
        autotools_stage_all
}




