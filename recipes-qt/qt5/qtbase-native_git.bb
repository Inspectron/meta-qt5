DESCRIPTION = "Native version of Qt/[X11|Mac|Embedded]"
DEPENDS = "zlib-native dbus-native"
SECTION = "libs"
HOMEPAGE = "http://qt-project.org"
LICENSE = "GFDL-1.3 & BSD & (LGPL-2.1 & Digia-Qt-LGPL-Exception-1.1 | LGPL-3.0)"
LIC_FILES_CHKSUM = " \
    file://LICENSE.LGPLv21;md5=d87ae0d200af76dca730d911474cbe5b \
    file://LICENSE.LGPLv3;md5=ffcfac38a32c9ebdb8ff768fa1702478 \
    file://LGPL_EXCEPTION.txt;md5=0145c4d1b6f96a661c2c139dfb268fb6 \
    file://LICENSE.FDL;md5=6d9f2a9af4c8b8c3c769f6cc1b6aaf7e \
"

QT_MODULE = "qtbase"

require qt5-native.inc
require qt5-git.inc

# common for qtbase-native, qtbase-nativesdk and qtbase
SRC_URI += "\
    file://0001-Add-linux-oe-g-platform.patch \
    file://0002-qlibraryinfo-allow-to-set-qt.conf-from-the-outside-u.patch \
    file://0003-Add-external-hostbindir-option.patch \
    file://0004-qt_module-Fix-pkgconfig-and-libtool-replacements.patch \
    file://0005-Revert-eglfs-Print-the-chosen-config-in-debug-mode.patch \
    file://0006-qeglplatformintegration-Undefine-CursorShape-from-X..patch \
    file://0007-configure-bump-path-length-from-256-to-512-character.patch \
    file://0008-eglfs-fix-egl-error-for-platforms-only-supporting-on.patch \
    file://0009-QOpenGLPaintDevice-sub-area-support.patch \
    file://0010-Make-Qt5GuiConfigExtras.cmake-find-gl-es-include-dir.patch \
"

# common for qtbase-native and nativesdk-qtbase
SRC_URI += " \
    file://0011-Always-build-uic.patch \
    file://0012-Add-external-hostbindir-option-for-native-sdk.patch \
"


CLEANBROKEN = "1"

QT_CONF_PATH = "${B}/qt.conf"

do_generate_qt_config_file() {
    :
}

EXTRA_OECONF = " \
    -prefix ${prefix} \
    -sysroot ${STAGING_DIR_NATIVE} \
    -no-gcc-sysroot \
    -system-zlib \
    -no-libjpeg \
    -no-libpng \
    -no-gif \
    -no-accessibility \
    -no-cups \
    -no-nis \
    -no-gui \
    -no-qml-debug \
    -no-sql-mysql \
    -sql-sqlite \
    -no-opengl \
    -no-openssl \
    -no-xcb \
    -no-icu \
    -verbose \
    -release \
    -prefix ${OE_QMAKE_PATH_PREFIX} \
    -bindir ${OE_QMAKE_PATH_BINS} \
    -libdir ${OE_QMAKE_PATH_LIBS} \
    -headerdir ${OE_QMAKE_PATH_HEADERS} \
    -archdatadir ${OE_QMAKE_PATH_ARCHDATA} \
    -datadir ${OE_QMAKE_PATH_DATA} \
    -docdir ${OE_QMAKE_PATH_DOCS} \
    -sysconfdir ${OE_QMAKE_PATH_SETTINGS} \
    -no-glib \
    -no-iconv \
    -silent \
    -nomake examples \
    -nomake tests \
    -no-rpath \
    -platform linux-oe-g++ \
"

# qtbase is exception, configure script is using our get(X)QEvalMakeConf and setBootstrapEvalVariable functions to read it from shell
export OE_QMAKE_COMPILER
export OE_QMAKE_CC
export OE_QMAKE_CFLAGS
export OE_QMAKE_CXX
export OE_QMAKE_CXXFLAGS
export OE_QMAKE_LINK
export OE_QMAKE_LDFLAGS
export OE_QMAKE_AR
export OE_QMAKE_STRIP

do_configure_prepend() {
    MAKEFLAGS="${PARALLEL_MAKE}" ${S}/configure -opensource -confirm-license ${EXTRA_OECONF} || die "Configuring qt failed. EXTRA_OECONF was ${EXTRA_OECONF}"
    bin/qmake ${OE_QMAKE_DEBUG_OUTPUT} ${S} -o Makefile || die "Configuring qt with qmake failed. EXTRA_OECONF was ${EXTRA_OECONF}"
}

do_install() {
    # Fix install paths for all
    find -name "Makefile*" | xargs sed -i "s,(INSTALL_ROOT)${STAGING_DIR_NATIVE}${STAGING_DIR_NATIVE},(INSTALL_ROOT)${STAGING_DIR_NATIVE},g"

    oe_runmake install INSTALL_ROOT=${D}

    if [ -d ${D}${STAGING_DIR_NATIVE}${STAGING_DIR_NATIVE} ] ; then
        echo "Some files are installed in wrong directory ${D}${STAGING_DIR_NATIVE}"
        cp -ra ${D}${STAGING_DIR_NATIVE}${STAGING_DIR_NATIVE}/* ${D}${STAGING_DIR_NATIVE}
        rm -rf ${D}${STAGING_DIR_NATIVE}${STAGING_DIR_NATIVE}
        # remove empty dirs
        TMP=`dirname ${D}/${STAGING_DIR_NATIVE}${STAGING_DIR_NATIVE}`
        while test ${TMP} != ${D}${STAGING_DIR_NATIVE}; do
            rmdir ${TMP}
            TMP=`dirname ${TMP}`;
        done
    fi

    # for modules which are still using syncqt and call qtPrepareTool(QMAKE_SYNCQT, syncqt)
    # e.g. qt3d, qtwayland
    ln -sf syncqt.pl ${D}${OE_QMAKE_PATH_QT_BINS}/syncqt
}

SRCREV = "2cb17c1fb903434274e58692c9f0df619affdab0"
