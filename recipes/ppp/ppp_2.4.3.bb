SECTION = "console/network"
DESCRIPTION = "Point-to-Point Protocol (PPP) daemon"
HOMEPAGE = "http://samba.org/ppp/"
DEPENDS = "libpcap"
LICENSE = "BSD GPLv2"
PR = "r5"

SRC_URI = "http://ppp.samba.org/ftp/ppp/ppp-${PV}.tar.gz \
        file://ppp-2.4.3-mppe-mppc-1.1.patch;patch=1 \
	file://makefile.patch;patch=1 \
	file://cifdefroute.patch;patch=1 \
	file://pppd-resolv-varrun.patch;patch=1 \
	file://plugins-fix-CC.patch;patch=1 \
	file://pppoatm-makefile.patch;patch=1 \
	file://enable-ipv6.patch;patch=1 \
	file://makefile-remove-hard-usr-reference.patch;patch=1 \
	file://ldflags.patch;patch=1 \
	file://pon \
	file://poff \
	file://init \
	file://ip-up \
	file://ip-down \
	file://08setupdns \
	file://92removedns"

SRC_URI_append_nylon = " file://ppp-tdbread.patch;patch=1"

inherit autotools

EXTRA_OEMAKE = "STRIPPROG=${STRIP} MANDIR=${D}${datadir}/man/man8 INCDIR=${D}/usr/include LIBDIR=${D}/usr/lib/pppd/${PV} BINDIR=${D}/usr/sbin"
EXTRA_OECONF = "--disable-strip"

do_stage () {
         make INCDIR=${STAGING_INCDIR} install-devel
}

do_install_append () {
	make install-etcppp ETCDIR=${D}/${sysconfdir}/ppp
	mkdir -p ${D}${bindir}/ ${D}${sysconfdir}/init.d
	mkdir -p ${D}${sysconfdir}/ppp/ip-up.d/
	mkdir -p ${D}${sysconfdir}/ppp/ip-down.d/
 	install -m 0755 ${WORKDIR}/pon ${D}${bindir}/pon
	install -m 0755 ${WORKDIR}/poff ${D}${bindir}/poff
	install -m 0755 ${WORKDIR}/init ${D}${sysconfdir}/init.d/ppp
	install -m 0755 ${WORKDIR}/ip-up ${D}${sysconfdir}/ppp/
	install -m 0755 ${WORKDIR}/ip-down ${D}${sysconfdir}/ppp/
	install -m 0755 ${WORKDIR}/08setupdns ${D}${sysconfdir}/ppp/ip-up.d/
	install -m 0755 ${WORKDIR}/92removedns ${D}${sysconfdir}/ppp/ip-down.d/
	rm -rf ${D}/${mandir}/man8/man8
}

CONFFILES_${PN} = "${sysconfdir}/ppp/pap-secrets ${sysconfdir}/ppp/chap-secrets ${sysconfdir}/ppp/options"
PACKAGES += "ppp-oa ppp-oe ppp-radius ppp-winbind ppp-minconn ppp-password ppp-tools"
FILES_${PN}        = "/etc /usr/bin /usr/sbin/chat /usr/sbin/pppd"
FILES_${PN}_nylon  = "/etc /usr/bin /usr/sbin/chat /usr/sbin/pppd /usr/sbin/tdbread"
FILES_${PN}-dbg += "${libdir}/pppd/2.4.3/.debug"
FILES_ppp-oa       = "/usr/lib/pppd/2.4.3/pppoatm.so"
FILES_ppp-oe       = "/usr/sbin/pppoe-discovery /usr/lib/pppd/2.4.3/rp-pppoe.so"
FILES_ppp-radius   = "/usr/lib/pppd/2.4.3/radius.so /usr/lib/pppd/2.4.3/radattr.so /usr/lib/pppd/2.4.3/radrealms.so"
FILES_ppp-winbind  = "/usr/lib/pppd/2.4.3/winbind.so"
FILES_ppp-minconn  = "/usr/lib/pppd/2.4.3/minconn.so"
FILES_ppp-password = "/usr/lib/pppd/2.4.3/pass*.so"
FILES_ppp-tools    = "/usr/sbin/pppstats /usr/sbin/pppdump"
DESCRIPTION_ppp-oa       = "Plugin for PPP needed for PPP-over-ATM"
DESCRIPTION_ppp-oe       = "Plugin for PPP needed for PPP-over-Ethernet"
DESCRIPTION_ppp-radius   = "Plugin for PPP that are related to RADIUS"
DESCRIPTION_ppp-winbind  = "Plugin for PPP to authenticate against Samba or Windows"
DESCRIPTION_ppp-minconn  = "Plugin for PPP to specify a minimum connect time before the idle timeout applies"
DESCRIPTION_ppp-password = "Plugin for PPP to get passwords via a pipe"
DESCRIPTION_ppp-tools    = "The pppdump and pppstats utitilities"
RDEPENDS_ppp_minconn    += "libpcap0.8"

pkg_postinst_${PN}() {
if test "x$D" != "x"; then
	exit 1
else
	chmod u+s ${sbindir}/pppd
fi
}
