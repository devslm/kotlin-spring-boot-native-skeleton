import _thread
import argparse as argparse
import logging
import os
import signal
import subprocess
import time

logging.basicConfig(format='%(asctime)s [%(levelname)-5s] - %(message)s')

logger = logging.getLogger('builder')
logger.setLevel("INFO")


GRAALVM_HOME_PATH = "/Users/ev.mareycheva/opt/graalvm-jdk-17/Contents/Home"
GRAALVM_JAVA_PATH = f"{GRAALVM_HOME_PATH}/bin/java"
NATIVE_IMAGE_RESOURCES_PATH = "./src/main/resources/META-INF/native-image"


def build_sources():
    subprocess.run(
        ['gradle', 'clean', 'build', '-x', 'test'],
        env={**os.environ, 'JAVA_HOME': GRAALVM_HOME_PATH},
    )


def start_aot_native_agent(aot_wait_timeout_minutes):
    process = subprocess.Popen(
        f'{GRAALVM_JAVA_PATH} "-Dspring.aot.enabled=true" -agentlib:native-image-agent=config-merge-dir={NATIVE_IMAGE_RESOURCES_PATH} -jar build/libs/kotlin-spring-boot-native-skeleton-1.0.0.jar',
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        shell=True
    )
    _thread.start_new_thread(print_aot_native_agent_logs, ('print-aot-logs', process.stdout))

    time.sleep(aot_wait_timeout_minutes * 60)

    os.kill(process.pid, signal.SIGTERM)


def print_aot_native_agent_logs(_, stdout):
    with stdout:
        for line in iter(stdout.readline, b''):
            print(line.decode("utf-8").strip())


def build_native_image():
    subprocess.run(['gradle', 'nativeCompile'])


if __name__ == "__main__":
    args_parser = argparse.ArgumentParser()
    args_parser.add_argument(
        '--disable-build-native-image',
        default=False,
        action='store_true',
        help='Disable build native image'
    )
    args_parser.add_argument(
        '--aot-wait',
        default=20,
        type=int,
        help='Terminate AOT stage after N minutes'
    )
    args = args_parser.parse_args()

    if args.disable_build_native_image:
        logger.info("Build native image step disabled. Run only build source and native-image-agent steps")

    build_sources()
    start_aot_native_agent(args.aot_wait)

    if not args.disable_build_native_image:
        build_native_image()
