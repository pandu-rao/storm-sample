#!/bin/bash

log_dir="/usr/local/share/storm/logs";

nimbus_log="$log_dir/nimbus.log";
supervisor_log="$log_dir/supervisor.log";
drpc_log="$log_dir/drpc.log";
ui_log="$log_dir/ui.log";

worker_log_6700="$log_dir/worker-6700.log";
worker_log_6701="$log_dir/worker-6701.log";
worker_log_6702="$log_dir/worker-6702.log";
worker_log_6703="$log_dir/worker-6703.log";


multitail -s 2 -Z red,black,inverse -T               \
          -x "%m %u@%h %f (%t) [%l]" -b 8            \
          -f -m 0 -n 77 -ci blue    $nimbus_log      \
          -f -m 0 -n 77 -ci blue    $supervisor_log  \
          -f -m 0 -n 77 -ci blue    $drpc_log        \
          -f -m 0 -n 77 -ci black   $ui_log          \
          -f -m 0 -n 77 -ci magenta $worker_log_6700 \
          -f -m 0 -n 77 -ci magenta $worker_log_6701 \
          -f -m 0 -n 77 -ci magenta $worker_log_6702 \
          -f -m 0 -n 77 -ci magenta $worker_log_6703 \
;
