#   -*- coding: utf-8 -*-
from pybuilder.core import use_plugin, init

use_plugin("python.core")
use_plugin("python.pycharm")
use_plugin("python.unittest")
use_plugin("python.coverage")
use_plugin("python.distutils")
# this plugin allows installing project dependencies with pip
use_plugin("python.install_dependencies")


name = "janusgraph_grpc_python"
default_task = ['clean', 'install_dependencies', 'prepare', 'compile_sources', 'package', 'publish']
version="0.0.1"


@init
def set_properties(project):
    project.set_property("coverage_break_build", False)  # default is True
    project.set_property("coverage_reset_modules", True)
    project.set_property("coverage_threshold_warn", 50)
    project.set_property("coverage_branch_threshold_warn", 50)
    project.set_property("coverage_branch_partial_threshold_warn", 50)
    project.set_property("coverage_allow_non_imported_modules", False)  # default is True
    project.set_property("coverage_exceptions", ["__init__"])
    pass
