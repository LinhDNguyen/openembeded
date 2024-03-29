# This machine lists common configuration options for Strongarm devices
# gcc does not differentiate between the different Strongarm versions, so neither do we
# http://lists.linuxtogo.org/pipermail/openembedded-devel/2008-January/004031.html

ARM_ABI = "oabi"
TARGET_CC_ARCH = "-march=armv4 -mtune=strongarm"
FEED_ARCH = "arm-oabi"

PACKAGE_EXTRA_ARCHS += "armv4 "
