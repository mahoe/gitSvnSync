package de.hoepmat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Eine Sammlung der Funktionen der alten Bash-Implementierung
 *
 *
 * Created by hoepmat on 11/17/15.
 */
@Component
public class ShellFunctionsArchive
{
    private static final Logger LOGGER = Logger.getLogger(ShellFunctionsArchive.class);

    private static final int ERR_SVN_CHECKOUT_REMOTE=100;
    private static final int ERR_SVN_FETCH=101;
    private static final int ERR_SVN_UPLOAD=102;
    private static final int ERR_SVN_MERGE_BACK=103;

    //#### NOT VCS ERRORS
    private static final int ERR_LOCK_STILL_PRESENT=200;
    private static final int ERR_STARTED_AT_THE_SAME_TIME=201;
    private static final int ERR_REMOVE_MESSAGE_FILE=202;

    private void logEcho(String message, String command){
//        function logecho {
//            local message="$1"
//            local command="$2"
//            if [ -z "$command" ]
//            then
//            echo "$message"
//            else
//            echo "$message"
//            echo "with command: $command"
//            (   set +e
//            eval "$command"
//            retVal=$?
//            if [ $retVal -ne 0 ]
//            then
//            echo "command returned $retVal" >&2
//            exit $retVal
//            fi
//            )
//            fi
//        }

        if(command==null || command.isEmpty())
        {
            LOGGER.debug(message);
        } else {
            // ergebnis des kommandos...
            String result = command;
            String logMessage = message + result;
            LOGGER.debug(logMessage);
        }

    }



    private void disableCommitMessageCheck(){
//    function disableCommitMessageCheck {
//            logecho "try to disable the update hook (by now commit messages are NOT checked anymore!)"
//            "/bin/mv $PATH_TO_UPDATE_HOOK $PATH_TO_UPDATE_HOOK.bak"
        }

    private void enableCommitMessageCheck(){
//        function enableCommitMessageCheck {
//            logecho "try to enable the update hook (by now commit messages are checked!)" "/bin/mv $PATH_TO_UPDATE_HOOK.bak $PATH_TO_UPDATE_HOOK"
    }

    private void checkoutTrackingBranchSvnAndFetchChanges(){
//        function checkoutTrackingBranchSvnAndFetchChanges {
//            logecho "--- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---"
//            logecho "try to checkout default svn remote ${DEFAULT_SVN_REMOTE}" "$GIT checkout $DEFAULT_SVN_REMOTE" || return $ERR_SVN_CHECKOUT_REMOTE
//            showCurrentState
//            logecho "try to fetch latest changes from SVN" "$GIT svn fetch" || return $ERR_SVN_FETCH
//            showCurrentState
//        }

    }

    private void mergeTrackingSvnTmpBranchBackToGit(){
//        #### This is only needed if there are no changes comming from git
//        function mergeTrackingSvnTmpBranchBackToGit {
//            logecho "try to merge the commits comming from svn" "$GIT merge $DEFAULT_SVN_REMOTE"
//            logecho "try to create a tmp branch to fetched changes from svn" "$GIT checkout -b tmp"
//            mergeBackToGit "tmp"
//            logecho "try to remove the tmp branch" "$GIT branch -d tmp"
//            showCurrentState
//        }

    }

    private void mergeGit2Svn(){
//        function mergeGit2Svn {
//            local MERGE_PRIORITY="$1"
//            local aux=0
//            checkoutTrackingBranchSvnAndFetchChanges || aux=$?
//
//            if [ "$aux" = "0" ]
//            then
//
//            # we save the message to have a starting ponit if something fails in the next steps
//            # Even if the message was just picked up from the same file, because we want to have a new changetime
//            # of that file
//            echo "$logMessage4Merge" > "$MANUAL_COMMIT_MSG_FILE.remove_this_suffix_after_editing"
//
//            logecho "--- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---"
//            if [ -z "$MERGE_PRIORITY" ]
//            then
//            logecho "try to merge with branch ${DEFAULT_GIT_MERGE_SOURCE_BRANCH}" "$GIT merge --no-ff -m '$logMessage4Merge' $DEFAULT_GIT_MERGE_SOURCE_BRANCH" || return 1
//            else
//            logecho "try to merge with branch ${DEFAULT_GIT_MERGE_SOURCE_BRANCH}" "$GIT merge --no-ff -X$MERGE_PRIORITY -m '$logMessage4Merge' $DEFAULT_GIT_MERGE_SOURCE_BRANCH" || return 2
//            fi
//            else
//            # send a mail and try it next time again (SVN was not available?)
//            mail2UsersWithErrorNoAndExit "$aux"
//            fi
//        }
    }

    private void showCurrentState(){
//        function showCurrentState {
//            logecho "--- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---"
//            logecho "My current directory is: $(pwd)"
//            logecho "The current status:" "$GIT status"
//            logecho "all branches:" "$GIT branch -avv"
//            logecho "--- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---"
//        }
    }

    private void mail2Users(){
//        function mail2Users {
//            logecho "mailing to >>>$logMessageMails<<< ..." "echo 'for details please have a look at the attachment' | $MAIL_COMMAND -s '$1' -a $DEFAULT_LOG_FILE -c '$logMessageMails' $DEFAULT_MAILS"
//        }
    }

    private void mail2UsersWithErrorNoAndExit(){
//        function mail2UsersWithErrorNoAndExit {
//            ERROR_MESSAGE=""
//            case "$1" in
//                $ERR_SVN_CHECKOUT_REMOTE)
//                ERROR_MESSAGE="Temporary Error (hopefully) - There was an error on checking out the svn remote!"
//                releaseLock
//                ;;
//                $ERR_SVN_FETCH)
//                ERROR_MESSAGE="Temporary Error (hopefully) - There was an error on fetching latest changes from svn!"
//                releaseLock
//                ;;
//                $ERR_SVN_UPLOAD)
//                ERROR_MESSAGE="ERROR - Problems on the last mile occured! There was a problem on sending the merge commit to the repositories (Svn). Check if there is a SIR / Issue No not accepted by SVN please!"
//                releaseLock
//                ;;
//                $ERR_SVN_MERGE_BACK)
//                ERROR_MESSAGE="ERROR - Problem on merge back the synchronization results to the git sync repository! Might be file permissions? Synchronization is stopped until the lock is removed and the problem fixed!"
//                #releaseLock  ... some one should have a deeper look what happend!
//            ;;
//                $ERR_STARTED_AT_THE_SAME_TIME)
//                ERROR_MESSAGE="Temporary Error - Synchronization was started twice at the same time."
//                releaseLock
//                ;;
//                $ERR_LOCK_STILL_PRESENT)
//                ERROR_MESSAGE="Permanent Error - Synchronization can not be executed because of a lock! Usualy this is because of an previous error..."
//                logecho "To remove the lock you can use the command on command line: echo > $PATH_TO_LOCK_FILE"
//            ;;
//                $ERR_REMOVE_MESSAGE_FILE)
//                ERROR_MESSAGE="Permanent Error - Can not remove the manual message file. Please have a look if there are problems with file permissions!"
//                ;;
//                *)
//                ERROR_MESSAGE="There was an unexpected error!"
//                ;;
//                esac
//
//                logecho "mailing to >>>$logMessageMails<<< ..." "echo 'ERROR $1 - $ERROR_MESSAGE For details please have a look at the attachment' | $MAIL_COMMAND -s 'Error $1' -a $DEFAULT_LOG_FILE -c '$logMessageMails' $DEFAULT_MAILS"
//                exit "$1"
//        }
    }

    private void printStartMessage(){
//        function printStartMessage {
//            logecho "################################################"
//            logecho "#### START SYNC AT $(date)"
//            logecho "################################################"
//        }
    }

    private void printSuccessMessage(){
//        function printSuccessMessage {
//            logecho "################################################"
//            logecho "#### SYNC FINISHED WITH SUCCESS"
//            logecho "################################################"
//        }
    }

    private void doCommitToSvn(){
//        function doCommitToSvn {
//            logecho "try to commit the merge to SVN" "$GIT svn dcommit" || return $ERR_SVN_UPLOAD
//            mergeBackToGit $DEFAULT_SVN_REMOTE || return $ERR_SVN_MERGE_BACK
//            rm -f "$MANUAL_COMMIT_MSG_FILE" || return $ERR_REMOVE_MESSAGE_FILE
//        }
    }

    private void mergeBackToGit(){
//        function mergeBackToGit {
//            local SVN_BRANCH="$1"
//            logecho "try to checkout branch ${DEFAULT_GIT_MERGE_SOURCE_BRANCH} go back to where we come from..." "$GIT checkout $DEFAULT_GIT_MERGE_SOURCE_BRANCH"
//            showCurrentState
//            logecho "try to merge back the incomming changes from SVN to branch ${DEFAULT_GIT_MERGE_SOURCE_BRANCH}." "$GIT merge $SVN_BRANCH"
//            if disableCommitMessageCheck
//                    then
//            logecho "update hook disabled"
//            else
//            mail2Users "WARNING! There was a problem on disabling the update hook... Please have a look at $PATH_TO_UPDATE_HOOK and or $DEFAULT_SHARED_REPOSITORY/hooks."
//            fi
//
//            logecho "try to push changes back to central repository $DEFAULT_SHARED_REPOSITORY." "$GIT push"
//
//            if enableCommitMessageCheck
//                    then
//            logecho "update hook active again..."
//            else
//            mail2Users "WARNING! There was a problem on reenabling the update hook... Please have a look at $PATH_TO_UPDATE_HOOK and or $DEFAULT_SHARED_REPOSITORY/hooks."
//            fi
//
//                    showCurrentState
//        }
    }

    private void startSyncGitANDSvn(){
//        function startSyncGitANDSvn {
//            logecho "try to enter the repository directory $DEFAULT_GATEWAY_REPOSITORY"
//            cd $DEFAULT_GATEWAY_REPOSITORY              # repeat because logecho ran the command in a subshell ...
//
//            logecho "--- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---"
//            logecho "Status of gateway repository BEFORE synchronization:"
//            showCurrentState
//
//            logecho "--- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---"
//            logecho "try to checkout branch ${DEFAULT_GIT_MERGE_SOURCE_BRANCH}" "$GIT checkout $DEFAULT_GIT_MERGE_SOURCE_BRANCH"
//            logecho "try to pull changes from central repository into branch ${DEFAULT_GIT_MERGE_SOURCE_BRANCH}" "$GIT pull --rebase"
//
//            logecho "--- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---"
//            logecho "extract the log for the new merge message"
//            logMessage4Merge=$($GIT log --pretty="%s %b %aN %aE see commit: %H" $DEFAULT_SVN_REMOTE.. | grep -v "git-svn-id: https:" | grep -v "^$" || true)
//            if [ -z "$logMessage4Merge" ]
//            then
//                    startOneWaySync
//            else
//            startTwoWaySync
//                    fi
//        }
    }

    private void startOneWaySync(){
//        function startOneWaySync {
//            logecho "Nothing to transport from Git branch $DEFAULT_GIT_MERGE_SOURCE_BRANCH to svn. Starting oneway synchronization..."
//
//            local aux=0
//            checkoutTrackingBranchSvnAndFetchChanges || aux=$?
//
//            if [ "$aux" = "0" ]
//            then
//            if mergeTrackingSvnTmpBranchBackToGit
//                    then
//            logecho "ONE WAY SYNC FINISHED"
//            else
//            mail2Users "Error on merging changes into $DEFAULT_GIT_MERGE_SOURCE_BRANCH!"
//
//            # this will stop all following synchronization attempts as long as there is the PID left in the lockfile!
//                    exit 1
//            fi
//            else
//            # send a mail and try it next time again (SVN was not available?)
//            mail2UsersWithErrorNoAndExit "$aux"
//            fi
//        }
    }

    private void startTwoWaySync(){
//        function startTwoWaySync {
//            logecho "Starting twoway synchronization..."
//            local aux=0
//
//            # escape " to be aware of quotation marks in commit messages
//            logMessage4Merge=$(echo "$logMessage4Merge" | sed s/'"'/'\\"'/g | sed s/"'"/'\\"'/g )
//            logMessage4Merge=$(echo "$logMessage4Merge" | sed s/'[\" ]*FORCE[: -]*'/' F_O_R_C_E - '/gi )
//
//            ### use provided text file for commit message instead of generated message
//            if [ -s "$MANUAL_COMMIT_MSG_FILE" ]
//            then
//                    logMessage4Merge=$(cat "$MANUAL_COMMIT_MSG_FILE")
//            fi
//            logecho "$logMessage4Merge"
//
//            logecho "--- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---"
//            logecho "extract the email addresses of all users who contibutes to the following merge commit"
//            logMessageMailsRaw=$($GIT log --pretty="%aE" $DEFAULT_SVN_REMOTE.. | grep -v "git-svn-id: https:" | sort -u | grep -v "^$" | grep -v "c7ci@" || true)
//            logMessageMails=$(echo -n $logMessageMailsRaw | tr -s ' \t\n' ',')
//
//            if mergeGit2Svn
//                    then
//            logecho "merged $DEFAULT_GIT_MERGE_SOURCE_BRANCH into $DEFAULT_SVN_REMOTE with success - Yeah!"
//            showCurrentState
//
//            doCommitToSvn || aux=$?
//
//            case "$aux" in
//                "0")
//                mail2Users "SUCCESS - The synchronization is finished. Thank you all ;-)"
//            ;;
//                $ERR_SVN_UPLOAD)
//                (
//                ### ignore errors because I try to solve a temp issue
//                set +e
//                logecho "I dont know if I am still in rebasing. Anyway I will try to cancel a rebase. A error messages can be ignored..." "$GIT rebase --abort"
//                logecho "Cleanu up the current branch." "$GIT reset --hard HEAD"
//                mail2UsersWithErrorNoAndExit "$aux"
//                )
//                ;;
//                *)
//                logecho "There was a problem to get the allready merged content commited to svn or pushed back to the git repositories."
//                logecho "I can not solve the problem. Now is your turn - please have a look in the log file (${DEFAULT_LOG_FILE})!"
//                mail2UsersWithErrorNoAndExit "$aux"
//            ;;
//                esac
//
//                else
//                if mergeGit2Svn $DEFAULT_MERGE_PRIORITY
//                then
//                logecho "WARNING! A problem on merge $DEFAULT_GIT_MERGE_SOURCE_BRANCH into $DEFAULT_SVN_REMOTE occured - see log files or mails!"
//                logecho "... you have to check and correct the files you can find in the email and in the log file (${DEFAULT_LOG_FILE})!"
//
//                logecho "--- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---"
//                logecho "... now I try to push all changes to the repositories."
//
//                aux=0
//                doCommitToSvn || aux=$?
//
//            case "$aux" in
//                "0")
//                mail2Users "WARNING - The synchronization is finished. But with merge conflict(s). These are automaticaly solved (I decided to use the SVN version...) please see the email content!)"
//            ;;
//                $ERR_SVN_UPLOAD)
//                (
//                ### ignore errors because I try to solve a temp issue
//                set +e
//                logecho "I dont know if I am still in rebasing. Anyway I will try to cancel a rebase. A error messages can be ignored..." "$GIT rebase --abort"
//                logecho "Cleanu up the current branch." "$GIT reset --hard HEAD"
//                mail2UsersWithErrorNoAndExit "$aux"
//                )
//                ;;
//                *)
//                logecho "There was a problem to get the allready merged content commited to svn or pushed back to the git repositories."
//                logecho "I can not solve the problem. Now is your turn - please have a look in the log file (${DEFAULT_LOG_FILE})!"
//                mail2Users "ERROR - Problems on the last mile occured! There was a problem on sending the merge commit to the repositories (Svn,Git, ...)"
//            ;;
//                esac
//                else
//                logecho "ERROR! A problem on merge $DEFAULT_GIT_MERGE_SOURCE_BRANCH into $DEFAULT_SVN_REMOTE occured - see log files or mails!"
//                logecho "I was not able to solve the problem automaticly! Please go to the gateway repo (DEFAULT_GATEWAY_REPOSITORY) and fix the problem manually... Sorry!"
//                mail2Users "ERROR - can not solve the merge conflict"
//                fi
//                        fi
//        }
    }

    /**
     * #### start the block, which covers the log output - THIS IS MORE OR LESS THE 'MAIN'
     */
    public void startSynchronize(){
//
//        (
//                source $HOME/.bash_profile
//
//                printStartMessage
//
//        # first we create a lock
//                createLock
//
//        startSyncGitANDSvn
//
//        # the last step is remove the lock
//                releaseLock
//
//        logecho "try to set the group writable bit to all objects in the path $DEFAULT_SHARED_REPOSITORY (this will end in many errors - yes... but it is needed ;-)" "chmod -f -R g+w $DEFAULT_SHARED_REPOSITORY"
//        logecho "try to set the group writable bit to all objects in the path $DEFAULT_GATEWAY_REPOSITORY (this will end in many errors - yes... but it is needed ;-)" "chmod -f -R g+w $DEFAULT_GATEWAY_REPOSITORY"
//
//        printSuccessMessage
//
//        ##### delete synclogfile after success?
//        # echo "" > $DEFAULT_LOG_FILE
//
//        #### the lines above sends the output to the console and to the log file
//        ) 2>&1 | (
//                echo "****************************************************" >> ${DEFAULT_LOG_FILE}
//        date >> ${DEFAULT_LOG_FILE}
//        tee -a ${DEFAULT_LOG_FILE}
//        date >> ${DEFAULT_LOG_FILE}
//        )
    }
}




