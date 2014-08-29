package com.togic.taskclean.service;

interface TaskClean
{
   void clean(boolean isKillSystem);
   void addWhiteList(String packageName);
}