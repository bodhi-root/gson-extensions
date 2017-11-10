GSON Extensions
===============

# Overview

For developers who prefer the simplicity of GSON over Jackson for JSON processing, here are some useful extensions to the core GSON library.  These include:

* JSON Pointer (RFC 6901)
* JSON Patch [draft-ietf-appsawg-json-patch-10](http://tools.ietf.org/html/draft-ietf-appsawg-json-patch-10)
* JSON Diff (RFC 6902)

This project is largely a re-write of existing libraries that used Jackson for these operations.  Special thanks to the developers of those projects for open-sourcing their libraries.  Because of their brilliant work, this project took about 2 days instead of 2 weeks.

Projects that were very helpful include:

* [json-patch](https://github.com/fge/json-patch) - patch, diff, and merge packages
* [jackson-coreutils](https://github.com/fge/jackson-coreutils) - JsonPointer and ReferenceToken classes