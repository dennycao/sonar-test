package com.sinitek.test;

public class error {
    # get parameter
    parser = argparse.ArgumentParser(description='manual to this script')
            parser.add_argument('--joburl', type=str)
            parser.add_argument('--user', type=str)
            parser.add_argument('--password', type=str)
            parser.add_argument('--jobname', type=str)
            parser.add_argument('--jobtype', type=str)
    args = parser.parse_args()
    jenkins_url = args.joburl
            jenkins_us = args.user
    jenkins_pw = args.password
            jenkins_jobname = args.jobname
    param_dict = {"JOB_TYPE": args.jobtype}

    # connect jenkins server
            server = jenkins.Jenkins(jenkins_url, username=jenkins_us, password=jenkins_pw)

    # run job
    server.build_job(name=jenkins_jobname, parameters=param_dict)

    print("Successful")
}
