SUMMARY = "Generic font configuration library"
DESCRIPTION = "Fontconfig is a font configuration and customization library, which \
does not depend on the X Window System. It is designed to locate \
fonts within the system and select them according to requirements \
specified by applications. \
Fontconfig is not a rasterization library, nor does it impose a \
particular rasterization library on the application. The X-specific \
library 'Xft' uses fontconfig along with freetype to specify and \
rasterize fonts."

HOMEPAGE = "http://www.fontconfig.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=fontconfig"

LICENSE = "MIT & MIT & PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=00252fd272bf2e722925613ad74cb6c7 \
                    file://src/fcfreetype.c;endline=45;md5=ce976b310a013a6ace6b60afa71851c1 \
                    file://src/fccache.c;beginline=1671;endline=1686;md5=906c2f04b0c79a1bcc84ecfca4de5619 \
                    "

SECTION = "libs"

DEPENDS = "expat freetype zlib gperf-native util-linux"

SRC_URI = "http://fontconfig.org/release/fontconfig-${PV}.tar.gz \
           file://revert-static-pkgconfig.patch \
           "

SRC_URI[sha256sum] = "ae480e9ca34382790312ff062c625ec70df94d6d9a9366e2b2b3d525f7f90387"

UPSTREAM_CHECK_REGEX = "fontconfig-(?P<pver>\d+\.\d+\.(?!9\d+)\d+)"

do_configure:prepend() {
    # work around https://bugs.freedesktop.org/show_bug.cgi?id=101280
    rm -f ${S}/src/fcobjshash.h ${S}/src/fcobjshash.gperf
}

do_install:append:class-target() {
    # duplicate fc-cache for postinstall script
    mkdir -p ${D}${libexecdir}
    ln ${D}${bindir}/fc-cache ${D}${libexecdir}/${MLPREFIX}fc-cache
}

do_install:append:class-nativesdk() {
    # duplicate fc-cache for postinstall script
    mkdir -p ${D}${libexecdir}
    ln ${D}${bindir}/fc-cache ${D}${libexecdir}/${MLPREFIX}fc-cache
}

PACKAGES =+ "fontconfig-utils"
FILES:${PN} =+ "${datadir}/xml/*"
FILES:${PN}-dev += "${datadir}/gettext/*"
FILES:fontconfig-utils = "${bindir}/* ${libexecdir}/*"

# Work around past breakage in debian.bbclass
RPROVIDES:fontconfig-utils = "libfontconfig-utils"
RREPLACES:fontconfig-utils = "libfontconfig-utils"
RCONFLICTS:fontconfig-utils = "libfontconfig-utils"
DEBIAN_NOAUTONAME:fontconfig-utils = "1"

inherit autotools pkgconfig relative_symlinks gettext

FONTCONFIG_CACHE_DIR ?= "${localstatedir}/cache/fontconfig"

# comma separated list of additional directories
# /usr/share/fonts is already included by default (you can change it with --with-default-fonts)
FONTCONFIG_FONT_DIRS ?= "no"

EXTRA_OECONF = " --disable-docs --with-default-fonts=${datadir}/fonts --with-cache-dir=${FONTCONFIG_CACHE_DIR} --with-add-fonts=${FONTCONFIG_FONT_DIRS}"

BBCLASSEXTEND = "native nativesdk"