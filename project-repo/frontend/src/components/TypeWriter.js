import React from "react";
import { useState,useEffect } from "react";
export default function TypeWriter({texts,speed,delayBetweenTexts}){
    const[displayedText,setDisplayedText]=useState('');
    const[index,setIndex]=useState(0);
    const[textIndex,setTextIndex]=useState(0);
    useEffect(()=>{
        if(textIndex < texts.length){
            if(index < texts[textIndex].length){
                const timeout=setTimeout(()=>{
                    setDisplayedText((prev)=>prev+texts[textIndex][index]);
                    setIndex((prev)=>prev+1);
                },speed);
                return ()=>clearTimeout(timeout);
            }
            else{
                const timeout=setTimeout(()=>{
                    setDisplayedText('');
                    setIndex(0);
                    setTextIndex((prev)=>(prev+1)%texts.length);
                },delayBetweenTexts);
                return()=>clearTimeout(timeout);
            }
        }
    },[index,textIndex,texts,speed,delayBetweenTexts]);
    return(
        <div>{displayedText}</div>
    );
}