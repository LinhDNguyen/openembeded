#Qt4 + boost image
require console-image.bb

DEPENDS += "qt4-embedded boost tslib"

IMAGE_INSTALL += "qt4-embedded \
                  tslib-calibrate tslib-tests \
                  boost-thread boost-date-time"

export IMAGE_BASENAME = "qt4-image"
