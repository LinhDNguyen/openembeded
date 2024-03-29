require glibc.inc

FILESDIR = "${@os.path.dirname(bb.data.getVar('FILE',d,1))}/glibc-cvs-2.3.5"
SRCDATE = "20050627"
PR = "r23"

#Doesnt build for sh3
DEFAULT_PREFERENCE_sh3="-1"

GLIBC_ADDONS ?= "ports,linuxthreads"

GLIBC_BROKEN_LOCALES = "sid_ET tr_TR mn_MN"

#
# For now, we will skip building of a gcc package if it is a uclibc one
# and our build is not a uclibc one, and we skip a glibc one if our build
# is a uclibc build.
#
# See the note in gcc/gcc_3.4.0.oe
#

python __anonymous () {
    import bb, re
    uc_os = (re.match('.*uclibc$', bb.data.getVar('TARGET_OS', d, 1)) != None)
    if uc_os:
        raise bb.parse.SkipPackage("incompatible with target %s" %
                                   bb.data.getVar('TARGET_OS', d, 1))
}

RDEPENDS_${PN}-dev = "linux-libc-headers-dev"
RPROVIDES_${PN}-dev += "libc-dev virtual-libc-dev"

#	   file://noinfo.patch;patch=1
#	   file://ldconfig.patch;patch=1;pnum=0
#	   file://arm-machine-gmon.patch;patch=1;pnum=0 \
#	   \
#	   file://arm-ioperm.patch;patch=1;pnum=0 \
#	   file://ldd.patch;patch=1;pnum=0 \
SRC_URI = "http://familiar.handhelds.org/source/v0.8.3/stash_libc_sources.redhat.com__20050627.tar.gz \
	   http://familiar.handhelds.org/source/v0.8.3/stash_ports_sources.redhat.com__20050627.tar.gz \
	   file://arm-audit.patch;patch=1 \
	   file://arm-audit2.patch;patch=1 \
	   file://arm-no-hwcap.patch;patch=1 \
	   file://arm-memcpy.patch;patch=1 \
	   file://arm-longlong.patch;patch=1;pnum=0 \
	   file://fhs-linux-paths.patch;patch=1 \
	   file://dl-cache-libcmp.patch;patch=1 \
	   file://ldsocache-varrun.patch;patch=1 \
	   file://5090_all_stubs-rule-fix.patch;patch=1 \
	   file://raise.patch;patch=1 \
	   file://zecke-sane-readelf.patch;patch=1 \
	   file://glibc-2.3.5-fix-weak-alias-arm.patch;patch=1 \
	   file://glibc-2.3.5-fix-weak-alias-arm-2.patch;patch=1 \
           file://etc/ld.so.conf \
	   file://generate-supported.mk"

# seems to fail on tls platforms
SRC_URI_append_arm = " file://dyn-ldconfig-20041128.patch;patch=1"
SRC_URI_append_armeb = " file://dyn-ldconfig-20041128.patch;patch=1"

# Build fails on sh3 and sh4 without additional patches
SRC_URI_append_sh3 = " file://no-z-defs.patch;patch=1 \
                       file://superh-fcntl.patch;patch=1;pnum=0"
SRC_URI_append_sh4 = " file://no-z-defs.patch;patch=1 \
                       file://superh-fcntl.patch;patch=1;pnum=0"

S = "${WORKDIR}/libc"
B = "${WORKDIR}/build-${TARGET_SYS}"

RDEPENDS_${PN}-dev = "linux-libc-headers-dev"
RPROVIDES_${PN}-dev += "libc-dev virtual-libc-dev"

EXTRA_OECONF = "--enable-kernel=${OLDEST_KERNEL} \
	        --without-cvs --disable-profile --disable-debug --without-gd \
		--enable-clocale=gnu \
	        --enable-add-ons=${GLIBC_ADDONS} \
		--with-headers=${STAGING_INCDIR \
		--without-selinux \
		${GLIBC_EXTRA_OECONF}"

EXTRA_OECONF += "${@get_glibc_fpu_setting(bb, d)}"

#avoid too much optimization on ppc
CFLAGS_dht-walnut += " -O1 -g0 -fPIC -fno-inline-functions -fno-unit-at-a-time "

do_munge() {
	# Integrate ports into tree
	mv ${WORKDIR}/ports ${S}

	# http://www.handhelds.org/hypermail/oe/51/5135.html
	# Some files were moved around between directories on
	# 2005-12-21, which means that any attempt to check out
	# from CVS using a datestamp older than that will be doomed.
	#
	# This is a workaround for that problem.
	rm -rf ${S}/bits
}

addtask munge before do_patch after do_unpack

export default_mmap_threshold_familiar = "32*1024"

do_configure () {
	if [ "x$default_mmap_threshold" != "x" ]; then
		echo "malloc-CPPFLAGS=-DDEFAULT_MMAP_THRESHOLD=\"(${default_mmap_threshold})\"" >configparms
	fi
# override this function to avoid the autoconf/automake/aclocal/autoheader
# calls for now
# don't pass CPPFLAGS into configure, since it upsets the kernel-headers
# version check and doesn't really help with anything
	if [ -z "`which rpcgen`" ]; then
		echo "rpcgen not found.  Install glibc-devel."
		exit 1
	fi
	(cd ${S} && gnu-configize) || die "failure in running gnu-configize"
	CPPFLAGS="" oe_runconf
}

rpcsvc = "bootparam_prot.x nlm_prot.x rstat.x \
	  yppasswd.x klm_prot.x rex.x sm_inter.x mount.x \
	  rusers.x spray.x nfs_prot.x rquota.x key_prot.x"

do_compile () {
	# -Wl,-rpath-link <staging>/lib in LDFLAGS can cause breakage if another glibc is in staging
	unset LDFLAGS
	base_do_compile
	(
		cd ${S}/sunrpc/rpcsvc
		for r in ${rpcsvc}; do
			h=`echo $r|sed -e's,\.x$,.h,'`
			rpcgen -h $r -o $h || oewarn "unable to generate header for $r"
		done
	)
}

require glibc-stage.inc

require glibc-package.bbclass
