package com.app.loginmvp.http

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader

/**

 * Create by wy on 2019/10/25 09:21

 */
class ShellUtils {
    private fun ShellUtils() {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {
        private val LINE_SEP = System.getProperty("line.separator")


        /**
         * Execute the command.
         *
         * @param command The command.
         * @param isRoot  True to use root, false otherwise.
         * @return the single [CommandResult] instance
         */
        fun execCmd(command: String, isRoot: Boolean): CommandResult {
            return execCmd(arrayOf(command), isRoot, true)
        }

        /**
         * Execute the command.
         *
         * @param commands The commands.
         * @param isRoot   True to use root, false otherwise.
         * @return the single [CommandResult] instance
         */
        fun execCmd(commands: List<String>?, isRoot: Boolean): CommandResult {
            return execCmd(commands?.toTypedArray(), isRoot, true)
        }

        /**
         * Execute the command.
         *
         * @param commands The commands.
         * @param isRoot   True to use root, false otherwise.
         * @return the single [CommandResult] instance
         */
        fun execCmd(commands: Array<String>, isRoot: Boolean): CommandResult {
            return execCmd(commands, isRoot, true)
        }

        /**
         * Execute the command.
         *
         * @param command         The command.
         * @param isRoot          True to use root, false otherwise.
         * @param isNeedResultMsg True to return the message of result, false otherwise.
         * @return the single [CommandResult] instance
         */
        fun execCmd(
            command: String,
            isRoot: Boolean,
            isNeedResultMsg: Boolean
        ): CommandResult {
            return execCmd(arrayOf(command), isRoot, isNeedResultMsg)
        }

        /**
         * Execute the command.
         *
         * @param commands        The commands.
         * @param isRoot          True to use root, false otherwise.
         * @param isNeedResultMsg True to return the message of result, false otherwise.
         * @return the single [CommandResult] instance
         */
        fun execCmd(
            commands: List<String>?,
            isRoot: Boolean,
            isNeedResultMsg: Boolean
        ): CommandResult {
            return execCmd(
                commands?.toTypedArray(),
                isRoot,
                isNeedResultMsg
            )
        }

        /**
         * Execute the command.
         *
         * @param commands        The commands.
         * @param isRoot          True to use root, false otherwise.
         * @param isNeedResultMsg True to return the message of result, false otherwise.
         * @return the single [CommandResult] instance
         */
        fun execCmd(
            commands: Array<String>?,
            isRoot: Boolean,
            isNeedResultMsg: Boolean
        ): CommandResult {
            var result = -1
            if (commands == null || commands.size == 0) {
                return CommandResult(result, "", "")
            }
            var process: Process? = null
            var successResult: BufferedReader? = null
            var errorResult: BufferedReader? = null
            var successMsg: StringBuilder? = null
            var errorMsg: StringBuilder? = null
            var os: DataOutputStream? = null
            try {
                process = Runtime.getRuntime().exec(if (isRoot) "su" else "sh")
                os = DataOutputStream(process!!.outputStream)
                for (command in commands) {
                    if (command == null) continue
                    os.write(command.toByteArray())
                    os.writeBytes(LINE_SEP)
                    os.flush()
                }
                os.writeBytes("exit$LINE_SEP")
                os.flush()
                result = process.waitFor()
                if (isNeedResultMsg) {
                    successMsg = StringBuilder()
                    errorMsg = StringBuilder()
                    successResult = BufferedReader(
                        InputStreamReader(
                            process.inputStream,
                            "UTF-8"
                        )
                    )
                    errorResult = BufferedReader(
                        InputStreamReader(
                            process.errorStream,
                            "UTF-8"
                        )
                    )
                    var line: String
                    line = successResult.readLine()
                    if (line != null) {
                        successMsg.append(line)
                        line = successResult.readLine()
                        while (line != null) {
                            successMsg.append(LINE_SEP).append(line)
                        }
                    }
                    line = errorResult.readLine()
                    if (line != null) {
                        errorMsg.append(line)
                        line = errorResult.readLine()
                        while (line != null) {
                            errorMsg.append(LINE_SEP).append(line)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                CloseUtils.closeIO(os!!, successResult!!, errorResult!!)
                process?.destroy()
            }
            return CommandResult(
                result,
                successMsg.toString(),
                errorMsg.toString()
            )
        }

        /**
         * The result of command.
         */
        class CommandResult(var result: Int, var successMsg: String, var errorMsg: String)
    }
}